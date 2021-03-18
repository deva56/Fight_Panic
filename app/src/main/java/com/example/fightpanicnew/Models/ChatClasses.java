package com.example.fightpanicnew.Models;

public class ChatClasses {

    public static class UserTyping {
        private String userName;
        private String roomName;

        public UserTyping(String userName, String roomName) {
            this.userName = userName;
            this.roomName = roomName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

    public static class Room {

        private String roomName;
        private String roomDescription;
        private String roomPassword;
        private String admin;
        private boolean roomPrivate;

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomDescription() {
            return roomDescription;
        }

        public void setRoomDescription(String roomDescription) {
            this.roomDescription = roomDescription;
        }

        public String getPassword() {
            return roomPassword;
        }

        public void setPassword(String roomPassword) {
            this.roomPassword = roomPassword;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public boolean getRoomPrivate() {
            return roomPrivate;
        }

        public void setRoomPrivate(boolean type) {
            this.roomPrivate = type;
        }
    }

    public static class initialData {
        private String username;
        private String roomName;
        private boolean isFromBackground;
        private boolean leaveWarning;

        public initialData(String username, String roomName, boolean isFromBackground, boolean leaveWarning) {
            this.username = username;
            this.roomName = roomName;
            this.isFromBackground = isFromBackground;
            this.leaveWarning = leaveWarning;
        }

        public boolean isLeaveWarning() {
            return leaveWarning;
        }

        public void setLeaveWarning(boolean leaveWarning) {
            this.leaveWarning = leaveWarning;
        }

        public boolean isFromBackground() {
            return isFromBackground;
        }

        public void setFromBackground(boolean fromBackground) {
            isFromBackground = fromBackground;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

    public static class sendMessage {
        private String userName;
        private String messageContent;
        private String roomName;
        private int viewType;
        private String profilePictureURL;

        public sendMessage(String userName, String messageContent, String roomName, int viewType) {
            this.userName = userName;
            this.messageContent = messageContent;
            this.roomName = roomName;
            this.viewType = viewType;
        }

        public sendMessage(String userName, String messageContent, String roomName, int viewType, String profilePictureURL) {
            this.userName = userName;
            this.messageContent = messageContent;
            this.roomName = roomName;
            this.viewType = viewType;
            this.profilePictureURL = profilePictureURL;
        }

        public String getProfilePictureURL() {
            return profilePictureURL;
        }

        public void setProfilePictureURL(String profilePictureURL) {
            this.profilePictureURL = profilePictureURL;
        }

        public int getViewType() {
            return viewType;
        }

        public void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }
    }

    public static class sendMessageUserInactiveOrActive {
        private String roomName;
        private String userName;

        public sendMessageUserInactiveOrActive(String roomName, String userName) {
            this.roomName = roomName;
            this.userName = userName;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
