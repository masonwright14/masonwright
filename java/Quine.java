package computation;

public class Quine {

    static String text;

    public static void main(String[] args) {
        a();
        b();
    }

    public static void a() {
        text = "    public static void b() {\n        String header = \"package computation;\\n\\npublic class Quine \"\n            + \"{\\n\\n    static String text;\\n\\n    \"\n            + \"public static void main(String[] args) \"\n            + \"{\\n        a();\\n        b();\\n    }\\n\\n\"\n            + \"    public static void a() {\\n        text = \\\"\";\n        System.out.print(header);\n\n        String unescapedText = text;\n        for (int i = unescapedText.length() - 1; i >= 0; i--) {\n            if (unescapedText.charAt(i) == '\\\"') {\n                unescapedText = unescapedText.substring(0, i) + \"\\\\\\\"\" + unescapedText.substring(i + 1);\n            } else if (unescapedText.charAt(i) == '\\n') {\n                unescapedText = unescapedText.substring(0, i) + \"\\\\n\" + unescapedText.substring(i + 1);\n            } else if (unescapedText.charAt(i) == '\\\\') {\n                unescapedText = unescapedText.substring(0, i) + \"\\\\\\\\\" + unescapedText.substring(i + 1);\n            }\n        }\n        System.out.print(unescapedText);\n        String interim = \"\\\";\\n    }\\n\\n\";\n        String footer = \"\\n}\\n\";\n        System.out.print(interim + text);\n        System.out.print(footer);\n    }";
    }

    public static void b() {
        String header = "package computation;\n\npublic class Quine "
            + "{\n\n    static String text;\n\n    "
            + "public static void main(String[] args) "
            + "{\n        a();\n        b();\n    }\n\n"
            + "    public static void a() {\n        text = \"";
        System.out.print(header);

        String unescapedText = text;
        for (int i = unescapedText.length() - 1; i >= 0; i--) {
            if (unescapedText.charAt(i) == '\"') {
                unescapedText = unescapedText.substring(0, i) + "\\\"" + unescapedText.substring(i + 1);
            } else if (unescapedText.charAt(i) == '\n') {
                unescapedText = unescapedText.substring(0, i) + "\\n" + unescapedText.substring(i + 1);
            } else if (unescapedText.charAt(i) == '\\') {
                unescapedText = unescapedText.substring(0, i) + "\\\\" + unescapedText.substring(i + 1);
            }
        }
        System.out.print(unescapedText);
        String interim = "\";\n    }\n\n";
        String footer = "\n}\n";
        System.out.print(interim + text);
        System.out.print(footer);
    }
}
