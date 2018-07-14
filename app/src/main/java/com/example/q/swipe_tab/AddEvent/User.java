package com.example.q.swipe_tab.AddEvent;

import java.io.Serializable;

public class User implements Serializable {
    String unique_id;
    String name;
    String nickname;

    public User(String id, String raw_name, String raw_alias){
        unique_id = id;
        name = raw_name;
        nickname = raw_alias;
    }
}
