package kr.co.dunet.app.goodallbeta.adapter.holder;

import java.io.Serializable;

/**
 * Created by hanbit on 2017-06-08.
 */

public class ListData implements Serializable{

    String roomName;
    String roomDate;
    String roomDescription;

    public ListData(String roomName, String roomDate, String roomDescription) {
        this.roomName = roomName;
        this.roomDate = roomDate;
        this.roomDescription = roomDescription;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomDate() {
        return roomDate;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    @Override
    public String toString() {
        return "ListData{" +
                "roomName='" + roomName + '\'' +
                ", roomDate='" + roomDate + '\'' +
                ", roomDescription='" + roomDescription + '\'' +
                '}';
    }
}
