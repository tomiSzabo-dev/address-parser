package hu.novaservices;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class LanguageSettings {
    private final Pattern[][] tokenexps;
    private final List<String> tokenfields;
    private final String[][][] dictionaries;
    @Getter
    private final String languageCode;
    private final int[] charweights;

    public LanguageSettings(String languageCode, List<String> tokenFields, String[][][] dictionaries, String[][] tokenexps, int[] charweights) {
        this.tokenfields = tokenFields.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (tokenexps == null)
            this.tokenexps = new Pattern[this.tokenfields.size()][0];
        else {
            this.tokenexps = new Pattern[this.tokenfields.size()][];

            for (int i = 0; i < this.tokenfields.size(); i++) {
                List<Pattern> exps = new ArrayList<>();
                if (tokenexps.length > i && tokenexps[i] != null) {
                    for (int j = 0; j < tokenexps[i].length; j++) {
                        String exp = tokenexps[i][j];
                        if (exp != null) {
                            exp = exp.trim();
                            if (!exp.isEmpty()) {
                                if (i == 0)
                                    exp = "\\A(" + exp + "(?:\\Z|\\s+))";
                                else if (i == this.tokenfields.size() - 1)
                                    exp = "(?:\\A|\\s+)(" + exp + "\\Z)";
                                else
                                    exp = "(?:\\A|\\s+)(" + exp + "(?:\\Z|\\s+))";

                                exps.add(Pattern.compile(exp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
                            }
                        }
                    }
                }
                this.tokenexps[i] = new Pattern[0];
                this.tokenexps[i] = exps.toArray(this.tokenexps[i]);
            }
        }

        if (dictionaries == null)
            this.dictionaries = new String[this.tokenfields.size()][0][2];
        else {
            this.dictionaries = new String[this.tokenfields.size()][][];

            for (int i = 0; i < this.tokenfields.size(); i++) {
                List<String[]> dictelems = new ArrayList<>();
                if (dictionaries.length > i && dictionaries[i] != null) {
                    for (int j = 0; j < dictionaries[i].length; j++) {
                        String[] dictelem = dictionaries[i][j];
                        if (dictelem != null && dictelem.length > 0) {
                            String dictkey = dictelem[0];
                            if (dictkey != null) {
                                dictkey = dictkey.trim();
                                if (!dictkey.isEmpty()) {
                                    String dictvalue = null;
                                    if (dictelem.length > 1) {
                                        dictvalue = dictelem[1];
                                    }

                                    if (dictvalue != null) {
                                        dictvalue = dictvalue.trim();
                                    } else
                                        dictvalue = "";

                                    dictelem = new String[]{dictkey, dictvalue};
                                    dictelems.add(dictelem);
                                }
                            }
                        }
                    }
                }
                this.dictionaries[i] = new String[0][];
                this.dictionaries[i] = dictelems.toArray(this.dictionaries[i]);
            }
        }

        this.charweights = new int[this.tokenfields.size()];
        if (charweights != null) {
            for (int i = 0; i < this.tokenfields.size() && i < charweights.length; i++) {
                this.charweights[i] = charweights[i];
            }
        }

        if (languageCode == null)
            this.languageCode = "";
        else
            this.languageCode = languageCode.trim();
    }


    public int getTokenCount() {
        return tokenfields.size();
    }

    /*
     * private int getDictionaryItemCount(int tokenNum) {
     * if(tokenNum>=getTokenCount())return 0; else return
     * dictionaries[tokenNum].length; }
     */

    public String getDictionaryValue(int tokenNum, String key) {
        if (key == null)
            return key;
        if (tokenNum >= getTokenCount())
            return key;

        key = key.trim();

        for (int i = 0; i < dictionaries[tokenNum].length; i++) {
            if (dictionaries[tokenNum][i][0].equalsIgnoreCase(key))
                return dictionaries[tokenNum][i][1];
        }

        return key;
    }

    public String getFieldName(int tokenNum) {
        if (tokenNum >= getTokenCount())
            return null;
        return tokenfields.get(tokenNum);
    }

    public int getTokenExpCount(int tokenNum) {
        if (tokenNum >= getTokenCount())
            return 0;
        else
            return tokenexps[tokenNum].length;
    }

    public Pattern getTokenExp(int tokenNum, int tokenExpNum) {
        if (tokenExpNum >= getTokenExpCount(tokenNum))
            return null;
        else
            return tokenexps[tokenNum][tokenExpNum];
    }

    public int getCharWeight(int tokenNum) {
        if (tokenNum >= getTokenCount())
            return 0;
        else
            return charweights[tokenNum];
    }

    public int whichField(String fieldName) {
        for (int i = 0; i < tokenfields.size(); i++)
            if (tokenfields.get(i).equalsIgnoreCase(fieldName))
                return i;
        return -1;
    }
}
