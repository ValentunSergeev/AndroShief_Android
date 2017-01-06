package com.valentun.androshief;

public class Constants {

    public static class URL {
        private static final String HOST = "https://androshief.herokuapp.com";

        public static final String INDEX = HOST + "/recipies.json";
        public static final String CREATE = HOST + "/recipies.json";

        public static final String REGISTER = HOST + "/auth/";
        public static final String SIGN_IN = HOST + "/auth/sign_in";
    }

    public static class DevURL {
        private static final String HOST = "https://androshief-212920.nitrousapp.com";

        public static final String INDEX = HOST + "/recipies.json";
        public static final String CREATE = HOST + "/recipies.json";
    }

    public static final String APP_PREFERENCES = "User";
}
