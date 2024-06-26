package com.simulation.LoLItemSimulation.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchTimelineDTO {
    private Info info;
    public Info getInfo() {
        return info;
    }

    public void setInfo() {
        this.info = info;
    }


    public static class Info {
        private List<Participant> participants;
        private List<Frame> frames;

        public List<Participant> getParticipants() {
            return participants;
        }

        public void setParticipants(List<Participant> participants) {
            this.participants = participants;
        }

        public List<Frame> getFrames() {
            return frames;
        }

        public void setFrames(List<Frame> frames) {
            this.frames = frames;
        }
    }

    public static class Participant {
        private int participantId;

        private String puuid;

        public int getParticipantId() {
            return participantId;
        }

        public void setParticipantId(int participantId) {
            this.participantId = participantId;
        }

        public String getPuuid() {
            return puuid;
        }

        public void setPuuid(String puuid) {
            this.puuid = puuid;
        }
    }

    public static class Frame {
        private List<Event> events;
        private int timestamp;
        private int lastTimestamp;
        private int beforeId;
        public List<Event> getEvents() {
            return events;
        }
        public void setEvents(List<Event> events) {
            this.events = events;
        }
        public int getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public int getLastTimestamp() {
            return lastTimestamp;
        }

        public void setLastTimestamp(int lastTimestamp) {
            this.lastTimestamp = lastTimestamp;
        }

        public int getBeforeId() {
            return beforeId;
        }

        public void setBeforeId(int beforeId) {
            this.beforeId = beforeId;
        }
    }
    public static class Event {
        private int itemId;
        private int beforeId;
        private int afterId;
        private int timestamp;
        private String type;
        private String levelUpType;
        private int skillSlot;
        private String wardType;
        private int participantId;
        private int controlWard;

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public int getBeforeId() {
            return beforeId;
        }

        public void setBeforeId(int beforeId) {
            this.beforeId = beforeId;
        }

        public int getAfterId() {
            return afterId;
        }

        public void setAfterId(int afterId) {
            this.afterId = afterId;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLevelUpType() {
            return levelUpType;
        }

        public void setLevelUpType(String levelUpType) {
            this.levelUpType = levelUpType;
        }

        public int getSkillSlot() {
            return skillSlot;
        }

        public void setSkillSlot(int skillSlot) {
            this.skillSlot = skillSlot;
        }

        public String getWardType() {
            return wardType;
        }

        public void setWardType(String wardType) {
            this.wardType = wardType;
        }

        public int getParticipantId() {
            return participantId;
        }

        public void setParticipantId(int participantId) {
            this.participantId = participantId;
        }

        public int getControlWard() {
            return controlWard;
        }

        public void setControlWard(int controlWard) {
            this.controlWard = controlWard;
        }
    }
}