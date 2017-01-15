/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import javax.naming.*;
import javax.naming.directory.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class STMPValidator {

    private static final String CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    private static Map<String, String> hear(BufferedReader in) throws IOException {
        String line = null;
        Map<String, String> result = new HashMap<>();
        int res = 0;

        while ((line = in.readLine()) != null) {
            System.out.println(line);
            String pfx = line.substring(0, 3);
            try {
                res = Integer.parseInt(pfx);
                result.put("code", String.valueOf(res));
            } catch (Exception ex) {
                res = -1;
                result.put("code", String.valueOf(res));
            }
            if (line.charAt(3) != '-') {
                break;
            }
        }
        result.put("description", line);
        return result;
    }

    private static void say(BufferedWriter wr, String text)
            throws IOException {
        System.out.println(text + "\r\n");
        wr.write(text + "\r\n");
        wr.flush();

        return;
    }

    private static ArrayList getMX(String hostName)
            throws NamingException {
        // Perform a DNS lookup for MX records in the domain
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial",
                "com.sun.jndi.dns.DnsContextFactory");
        DirContext ictx = new InitialDirContext(env);
        Attributes attrs = ictx.getAttributes(hostName, new String[]{"MX"});
        Attribute attr = attrs.get("MX");

        // if we don't have an MX record, try the machine itself
        if ((attr == null) || (attr.size() == 0)) {
            attrs = ictx.getAttributes(hostName, new String[]{"A"});
            attr = attrs.get("A");
            if (attr == null) {
                throw new NamingException("No match for name '" + hostName + "'");
            }
        }
        // Huzzah! we have machines to try. Return them as an array list
        // NOTE: We SHOULD take the preference into account to be absolutely
        //   correct. This is left as an exercise for anyone who cares.
        ArrayList res = new ArrayList();
        NamingEnumeration en = attr.getAll();

        while (en.hasMore()) {

            String mailhost;
            String x = (String) en.next();
            String f[] = x.split(" ");
            //  THE fix *************
            if (f.length == 1) {
                mailhost = f[0];
            } else if (f[1].endsWith(".")) {
                mailhost = f[1].substring(0, (f[1].length() - 1));
            } else {
                mailhost = f[1];
            }
            //  THE fix *************            
            res.add(mailhost);
        }
        return res;
    }

    /**
     * Validate the email and log the error in log file
     *
     * @param address the email address to be validated
     * @param sender the sender email to check from.
     * @return true if the email is valid. Otherwise, it returns false
     */
    public static boolean isAddressValid(String address, String sender) {
        String senderDomain = "ngc-eg.com";
        Map<String, String> result = new HashMap<>();
        try (BufferedOutputStream bufWriter = new BufferedOutputStream(new FileOutputStream("Emails Validation Report.xml", false));) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer;
            writer = new XMLWriter(bufWriter, format);
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("Emails");
            Element emailElement;

            // Find the separator for the domain name
            int pos = address.indexOf('@');
            // If the address does not contain an '@', it's not valid
            if (pos == -1) {
                return false;
            }
            // Isolate the domain/machine name and get a list of mail exchangers
            String domain = address.substring(++pos);
            ArrayList mxList = null;

            emailElement = root.addElement("Email").addAttribute("domain", domain);

            try {
                mxList = getMX(domain);
            } catch (NamingException ex) {
                Element mxElement = emailElement.addElement("mx_domain")
                        .addAttribute("name", "Not Valid MX");

                mxElement.addElement("From").addText(sender);
                mxElement.addElement("To").addText(address);
                Element errorsElement = mxElement.addElement("Errors");
                errorsElement.addElement("error")
                        .addAttribute("code", "0")
                        .addText("Error while resolving MX domains");
                return false;
            }
            // Just because we can send mail to the domain, doesn't mean that the
            // address is valid, but if we can't, it's a sure sign that it isn't
            if (mxList.isEmpty()) {
                Element mxElement = emailElement.addElement("mx_domain")
                        .addAttribute("name", "Empty MX Domain List");

                mxElement.addElement("From").addText(sender);
                mxElement.addElement("To").addText(address);
                Element errorsElement = mxElement.addElement("Errors");
                errorsElement.addElement("error")
                        .addAttribute("code", "0")
                        .addText("There is no MX domains");
                return false;
            }
            // Now, do the SMTP validation, try each mail exchanger until we get
            // a positive acceptance. It *MAY* be possible for one MX to allow
            // a message [store and forwarder for example] and another [like
            // the actual mail server] to reject it. This is why we REALLY ought
            // to take the preference into account.
            for (int mx = 0; mx < mxList.size(); mx++) {
                Element mxElement = emailElement.addElement("mx_domain")
                        .addAttribute("name", mxList.get(mx).toString());

                mxElement.addElement("From").addText(sender);
                mxElement.addElement("To").addText(address);
                Element errorsElement = mxElement.addElement("Errors");

                boolean valid = false;
                try {

                    int res;
                    int code;
                    //
                    Socket skt = new Socket((String) mxList.get(mx), 25);

                    BufferedReader rdr = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                    BufferedWriter wtr = new BufferedWriter(new OutputStreamWriter(skt.getOutputStream()));

                    result = hear(rdr);
                    //res = hear(rdr);
                    //if (res != 220) {
                    code = Integer.valueOf(result.get("code"));
                    if (code != 220) {
                        System.out.println("Invalid header");
                        errorsElement.addElement("error")
                                .addAttribute("code", result.get("code"))
                                .addText(result.get("description"));
                        //throw new Exception("Invalid header");
                    }

                    say(wtr, "EHLO " + senderDomain);

                    result = hear(rdr);
                    code = Integer.valueOf(result.get("code"));
                    //res = hear(rdr);
                    if (code != 250) {
                        System.out.println("Not ESMTP");
                        errorsElement.addElement("error")
                                .addAttribute("code", result.get("code"))
                                .addText(result.get("description"));
                        //throw new Exception("Not ESMTP");
                    }
                    /* //For authentication with the server
                    say(wtr, "AUTH LOGIN");
                    res = hear(rdr);
                    if (res != 334) {
                    System.out.println("Invalid Auth request");
                    throw new Exception("Invalid Auth request");
                    }
                    /*
                    say(wtr, base64Encode("omar@ngc-eg.com".getBytes()));
                    res = hear(rdr);
                    if (res != 334) {
                    System.out.println("Invalid username");
                    throw new Exception("Invalid username");
                    }
                    say(wtr, base64Encode("mendl970552".getBytes()));
                    res = hear(rdr);
                    if (res != 235) {
                    System.out.println("Auth Failed");
                    throw new Exception("Auth Failed");
                    }
                     */
                    // validate the sender address
                    say(wtr, "MAIL FROM: <" + sender + ">");
                    result = hear(rdr);
                    code = Integer.valueOf(result.get("code"));
                    //res = hear(rdr);
                    if (code != 250) {
                        System.out.println("Sender rejected");
                        errorsElement.addElement("error")
                                .addAttribute("code", result.get("code"))
                                .addText(result.get("description"));
                        //throw new Exception("Sender rejected");
                    }

                    say(wtr, "RCPT TO: <" + address + ">");
                    result = hear(rdr);
                    code = Integer.valueOf(result.get("code"));
                    //res = hear(rdr);
                    if (code != 250) {

                        System.out.println("Res code = " + code);
                        errorsElement.addElement("error")
                                .addAttribute("code", result.get("code"))
                                .addText(result.get("description"));
                        //throw new Exception("Address is not valid!");
                    }
                    // be polite
                    say(wtr, "RSET");
                    hear(rdr);
                    say(wtr, "QUIT");
                    String line;

                    while ((line = rdr.readLine()) != null) {
                        System.out.println(line);
                    }

                    hear(rdr);
                    

                    valid = true;

                    rdr.close();
                    wtr.close();
                    skt.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException | NumberFormatException ex) {
                    // Do nothing but try next host
                    errorsElement.addElement("error")
                            .addAttribute("code", "0")
                            .addText(ex.toString());
                } finally {
                    if (valid) {
                        return true;
                    } 
                }

            }
            writer.write(document);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(STMPValidator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(STMPValidator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private static String base64Encode(byte[] in) {

        StringBuilder out = new StringBuilder((in.length * 4) / 3);
        int b;
        for (int i = 0; i < in.length; i += 3) {
            b = (in[i] & 0xFC) >> 2;
            out.append(CODES.charAt(b));
            b = (in[i] & 0x03) << 4;
            if (i + 1 < in.length) {
                b |= (in[i + 1] & 0xF0) >> 4;
                out.append(CODES.charAt(b));
                b = (in[i + 1] & 0x0F) << 2;
                if (i + 2 < in.length) {
                    b |= (in[i + 2] & 0xC0) >> 6;
                    out.append(CODES.charAt(b));
                    b = in[i + 2] & 0x3F;
                    out.append(CODES.charAt(b));
                } else {
                    out.append(CODES.charAt(b));
                    out.append('=');
                }
            } else {
                out.append(CODES.charAt(b));
                out.append("==");
            }
        }

        return out.toString();
    }

    private static byte[] base64Decode(String input) {
        if (input.length() % 4 != 0) {
            throw new IllegalArgumentException("Invalid base64 input");
        }
        byte decoded[] = new byte[((input.length() * 3) / 4) - (input.indexOf('=') > 0 ? (input.length() - input.indexOf('=')) : 0)];
        char[] inChars = input.toCharArray();
        int j = 0;
        int b[] = new int[4];
        for (int i = 0; i < inChars.length; i += 4) {
            // This could be made faster (but more complicated) by precomputing these index locations.
            b[0] = CODES.indexOf(inChars[i]);
            b[1] = CODES.indexOf(inChars[i + 1]);
            b[2] = CODES.indexOf(inChars[i + 2]);
            b[3] = CODES.indexOf(inChars[i + 3]);
            decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
            if (b[2] < 64) {
                decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
                if (b[3] < 64) {
                    decoded[j++] = (byte) ((b[2] << 6) | b[3]);
                }
            }
        }

        return decoded;
    }
}
