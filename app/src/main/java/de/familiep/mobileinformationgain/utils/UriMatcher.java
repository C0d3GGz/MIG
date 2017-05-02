package de.familiep.mobileinformationgain.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriMatcher {

    public static String getShortenedDomainIfUri(String possibleUri){
        try {
            URI uri = new URI(possibleUri);
            String host = uri.getHost();
            if(host == null) {
                return matchesRegex(possibleUri);
            }
            return host;
        } catch (URISyntaxException e){
            return matchesRegex(possibleUri);
        }
    }

    private static String matchesRegex(String input){
        if(Pattern.matches("(\\w+\\.)+\\w+\\/.*", input))
            return input.split("/")[0];

        else if(Pattern.matches("(\\w+\\.)+\\w+\\?.*", input))
            return input.split("\\?")[0];

        else if(Pattern.matches("(\\w+\\.)+\\w+#.*", input))
            return input.split("#")[0];
        else
            return input;
    }
}
