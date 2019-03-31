package com.nelolik.pocketvocabulary;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestSplitString {

    String input1 = "белый, чёрный, красный";
    String input2 = "белый чёрный. красный";
    String input3= "белый\\чёрный/красный";
    String input4 = "белый  чёрный \nкрасный";
    String input5 = "белый  Йошкарола \nкрасный";
    String input6 = "что-то  кто_нибудь \nкакраз";
    String result = "белый, чёрный, красный";
    String result2 = "белый, Йошкарола, красный";
    String result3 = "что-то, кто_нибудь, какраз";

    @Test
    public void splitIsCorrect() {
//        assertEquals(result, MainListFragment.formatTranslationString(input1));
//        assertEquals(result, MainListFragment.formatTranslationString(input2));
//        assertEquals(result, MainListFragment.formatTranslationString(input3));
//        assertEquals(result, MainListFragment.formatTranslationString(input4));
//        assertEquals(result2, MainListFragment.formatTranslationString(input5));
//        assertEquals(result3, MainListFragment.formatTranslationString(input6));
    }
}
