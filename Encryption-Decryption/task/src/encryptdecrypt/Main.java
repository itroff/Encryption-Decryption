package encryptdecrypt;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

class Context {
    private CipherAlgorithm algorithm;

    public void setAlgorithm(CipherAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String decrypt(String text, int key) {
        return this.algorithm.decrypt(text, key);
    }

    public String encrypt(String text, int key) {
        return this.algorithm.encrypt(text, key);
    }
}

interface CipherAlgorithm {

    String decrypt(String text, int key);

    String encrypt(String text, int key);
}

class  UnicodeAlgorithm implements CipherAlgorithm {

    @Override
    public String decrypt(String text, int key) {
        String strDec = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) + key > 0) {
                strDec += (char) (text.charAt(i) - key);
            } else {
                strDec += (char) (text.charAt(i) - key + 127);
            }
        }
        return strDec;
    }

    @Override
    public String encrypt(String text, int key) {
        String strEnc = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) + key < 128) {
                strEnc += (char) (text.charAt(i) + key);
            } else {
                strEnc += (char) (text.charAt(i) + key - 127);
            }
        }
        return strEnc;
    }
}

class ShiftAlgorithm implements CipherAlgorithm {

    @Override
    public String decrypt(String text, int key) {
        String strEnc = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 96 && text.charAt(i) < 123) {
                if (text.charAt(i) - key > 96) {
                    strEnc += (char) (text.charAt(i) - key);
                } else {
                    strEnc += (char) (123 - (97 - (text.charAt(i) - key)));//101 -> 117
                }
            } else {
                strEnc += (char) text.charAt(i);
            }
        }
        return strEnc;
    }

    @Override
    public String encrypt(String text, int key) {
        String strEnc = "";
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 96 && text.charAt(i) < 123) {
                if (text.charAt(i) + key < 123) {
                    strEnc += (char) (text.charAt(i) + key);
                } else {
                    strEnc += (char) (97 + (text.charAt(i) + key) - 123);
                }
            } else {
                strEnc += (char) text.charAt(i);
            }
        }
        return strEnc;
    }
}


public class Main {

    public static void main(String[] args) {
        String cmd = "enc";
        String str = "";
        int key = 0;
        String outFile = "";
        String inFile = "";
        String alg = "shift";
        for (int i = 0; i < args.length; i++) {
            if ("-mode".equals(args[i])) {
                cmd = args[i + 1];
                i++;
            } else if ("-data".equals(args[i])) {
                str = args[i + 1];
                i++;
            } else if ("-key".equals(args[i])) {
                key = Integer.parseInt(args[i + 1]);
                i++;
            } else if ("-in".equals(args[i])){
                inFile = args[i + 1];
            } else if ("-out".equals(args[i])) {
                outFile = args[i + 1];
            } else if ("-alg".equals(args[i])) {
                alg = args[i + 1];
            }
        }
        if (str.isEmpty() && !inFile.isEmpty()) {
            File file = new File(inFile);
            try (Scanner scanner = new Scanner(file)) {
                str = scanner.nextLine();
            } catch (Exception ex) {
                System.out.println("Error: not found");
            }
        }
        String result = "";
        Context ctx = new Context();
        if ("shift".equals(alg)) {
            ctx.setAlgorithm(new ShiftAlgorithm());
        } else {
            ctx.setAlgorithm(new UnicodeAlgorithm());
        }
        if ("enc".equals(cmd)) {
            result = ctx.encrypt(str, key);
        } else if ("dec".equals(cmd)) {
            result = ctx.decrypt(str, key);
        }
        if (outFile.isEmpty()) {
            System.out.println(result);
        } else {
            File file = new File(outFile);
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(result);
            } catch (Exception ex) {
                System.out.println("Error: write");
            }
        }
    }
}
