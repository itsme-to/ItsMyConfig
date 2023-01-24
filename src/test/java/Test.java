import ua.realalpha.itsmyconfig.progress.ProgressBar;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Test {

    private static final Pattern END_PATTERN = Pattern.compile("<+/+\\w+>");

    public static void main(String[] args) {

        System.out.println(Tag.getContent("actionbar", "<actionbar>%itsme_placeholder_essentialsPrefix% %itsme_placeholder_textColor%Essentials <yellow>{0}</yellow> a été rechargé"));
        System.out.println(Tag.messageWithoutTag("actionbar", Tag.getContent("actionbar", "<actionbar>%itsme_placeholder_essentialsPrefix% %itsme_placeholder_textColor%Essentials <yellow>{0}</yellow> a été rechargé")));
        System.out.println(Tag.getTags("<hello:1:2:coucou:\"aa\">coucou wesh <bonsoir:coucou>oui cc </hello><test>aaa</test>"));
        System.out.println(Tag.messageWithoutTag("hello", getContentInsideTag("hello", "<hello:1:2:coucou>coucou asas <va></hello> wesh <bonsoir:coucou>oui cc <test>aaa</test>")));

        ProgressBar progressBar = new ProgressBar("a", "⦁●⚫●⚫●⚫●⚫●⚫●⚫●⚫●⚫●⚫●⚫●⦁", "green", "orange", "gris");
        System.out.println(progressBar.render(1, 23));

        String message = "<bold>$<gradient:#df00fb:#8403b9>MAGICBLOCK</gradient></bold> <white>┃ vous a invité sur son île.";
        System.out.println(Tag.getTags(message));
        System.out.println(message.substring(message.indexOf("$")+1));
    }


    // get content inside tag from message (ex: <hello:1:2:coucou>coucou</hello> wesh <bonsoir:coucou>oui cc <test>aaa</test> -> <hello:1:2:coucou>coucou</hello>
    public static String getContentInsideTag(String tag, String message){
        String tagStart = "<"+tag;
        String tagEnd = "</"+tag+">";
        int start = message.indexOf(tagStart);
        int end = message.indexOf(tagEnd);

        if (start == -1 || end == -1) return null;

        return message.substring(start, end+tagEnd.length());
    }

    public static String removeContentInsideTag(String tag, String message){
        String tagStart = "<"+tag;
        String tagEnd = "</"+tag+">";
        int start = message.indexOf(tagStart);
        int end = message.indexOf(tagEnd);
        return message.substring(0, start) + message.substring(end+tagEnd.length());
    }


    // extract parameter of token (ex: <hello:1:2:coucou>coucou <bonsoir:coucou>oui</hello> -> [1, 2, coucou]) and remove token from message
    public static String[] extractParameterOfToken(String message) {
        int border = message.indexOf('>');
        String token = message.substring(0, border+1);
        String[] parameters = token.substring(1, token.length()-1).split(":");
        return Arrays.copyOfRange(parameters, 1, parameters.length);
    }

}
