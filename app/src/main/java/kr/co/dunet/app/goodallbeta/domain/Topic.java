package kr.co.dunet.app.goodallbeta.domain;

/**
 * Created by hanbit on 2017-06-30.
 */

public class Topic {

    private String type;
    private String rid;
    private String id;
    private String roomCode;
    private String roomName;
    private String nickName;
    private String message;
    private String regDate;
    private String key;


    public Topic(String rid, String id, String roomCode, String roomName, String nickName, String message, String regDate, String key) {
        this.rid = rid;
        this.id = id;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.nickName = nickName;
        this.message = message;
        this.regDate = regDate;
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public String getRid() {
        return rid;
    }

    public String getId() {
        return id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getMessage() {
        return message;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getKey() {
        return key;
    }
}
