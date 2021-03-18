package com.example.fightpanicnew;

public class Constants {

    //Changeable private IP address of a development computer for local testing (server purposes)
    /*public static final String BASE_URL = "http://192.168.1.17:8080/api/v1/";
    public static final String SOCKET_URL = "http://192.168.1.17:8080";*/

    public static final String BASE_URL = "https://fight-panic.herokuapp.com/api/v1/";
    public static final String SOCKET_URL = "https://fight-panic.herokuapp.com/";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String REGISTRATION_OR_LOGIN_OR_SKIP_MENU = "registrationOrLoginOrSkipMenu";

    public static final String IS_LOGGED_IN_DATA = "isLoggedInData";
    public static final String IS_LOGGED_IN_STATE = "isLoggedInState";

    public static final String LOGIN_INFORMATION = "loginInformation";
    public static final String LOGIN_EMAIL = "loginEmail";
    public static final String LOGIN_TOKEN = "loginToken";
    public static final String LOGGED_USER_ID = "loggedUserID";

    public static final String EDIT_TEXT_FIELDS_INFORMATION = "editTextFieldsInformation";
    public static final String EDIT_TEXT_FIELDS_USERNAME = "editTextFieldsUsername";
    public static final String EDIT_TEXT_FIELDS_EMAIL = "editTextFieldsEmail";
    public static final String EDIT_TEXT_FIELDS_PASSWORD = "editTextFieldsPassword";

    public static final String REGISTER_USER_INFORMATION_EDIT_TEXT_FIELDS_INFORMATION = "registerUserInformationEditTextFieldsInformation";
    public static final String NUMBER_PICKER_AGE = "numberPickerAge";
    public static final String EDIT_TEXT_FIELDS_LOCATION = "editTextFieldsLocation";
    public static final String EDIT_TEXT_FIELDS_OCCUPATION = "editTextFieldsOccupation";
    public static final String EDIT_TEXT_FIELDS_SHORT_DESCRIPTION = "editTextFieldsShortDescription";
    public static final String EDIT_TEXT_FIELDS_GENDER_TEXT = "editTextFieldsGenderText";
    public static final String EDIT_TEXT_FIELDS_GENDER_ID = "editTextFieldsGenderID";
    public static final String EDIT_TEXT_FIELDS_GENDER_STATE = "editTextFieldsGenderState";
    public static final String EDIT_TEXT_FIELDS_GENDER_HIDDEN = "editTextFieldsGenderHidden";
    public static final String EDIT_TEXT_FIELDS_FIRST_NAME = "editTextFieldsFirstName";
    public static final String EDIT_TEXT_FIELDS_LAST_NAME = "editTextFieldsLastName";

    //Konstante korištene u panic calendar-u i pill diary-u
    public static final String EXTRA_TITLE = "extraTitle";
    public static final String EXTRA_DESCRIPTION = "extraDescription";
    public static final String EXTRA_RECORD_CREATION_DATE = "extraRecordCreationDate";
    public static final String EXTRA_STRENGTH = "extraStrength";
    public static final String EXTRA_ATTACK_DATE = "extraAttackDate";
    public static final String EXTRA_ATTACK_TIME = "extraAttackTime";
    public static final String EXTRA_ID = "extraID";
    public static final String EXTRA_SHOULD_DELETE = "extraShouldDelete";

    //Konstante za opcije
    public static final String DARK_MODE_SWITCH = "darkMode";
    public static boolean DARK_MODE_VALUE = false;

    //Konstante za chat
    public static final String IS_BACKGROUND_TERMINATED = "backgroundTerminated";
    public static final String IS_BACKGROUND_TERMINATED_VALUE = "false";
    public static final String SHOW_LEAVE_WARNING = "leaveWarning";
    public static final String SHOW_LEAVE_WARNING_VALUE = "false";
    public static final String FOREGROUND_SERVICE_ROOM_NAME = "roomName";
    public static final String FOREGROUND_SERVICE_PROFILE_PICTURE_URL = "profilePictureURL";
    public static final String FOREGROUND_SERVICE_USER_NAME = "userName";
    public static final String FOREGROUND_SERVICE_REMOTE_BUILDER_KEY = "replyKey";
    public static final String FOREGROUND_SERVICE_DIRECT_REPLY_RECEIVER_LOCK = "receiverLock";
    public static final String FOREGROUND_SERVICE_DIRECT_REPLY_TEXT = "directReplyText";
}
