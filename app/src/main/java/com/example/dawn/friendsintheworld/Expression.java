package com.example.dawn.friendsintheworld;

import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;


public class Expression {
        public String message;

        public Expression(String message){
            this.message=message;
        }

        public static Expression register(String groupName, String memberName) throws IOException {
            StringWriter stringWriter = new StringWriter();
            JsonWriter writer = new JsonWriter( stringWriter );
            writer.beginObject()
                    .name("type").value("register")
                    .name("group").value(groupName)
                    .name("member").value(memberName)
                    .endObject();
            return new Expression(stringWriter.toString());
        }

    public static Expression unregister(String id) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter( stringWriter );
        writer.beginObject()
                .name("type").value("unregister")
                .name("id").value(id)
                .endObject();
        return new Expression(stringWriter.toString());
    }

    public static Expression members(String groupName) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter( stringWriter );
        writer.beginObject()
                .name("type").value("members")
                .name("group").value(groupName)
                .endObject();
        return new Expression(stringWriter.toString());
    }

    public static Expression currentGroups() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter( stringWriter );
        writer.beginObject()
                .name("type").value("groups")
                .endObject();
        return new Expression(stringWriter.toString());
    }

    public static Expression setPosition(String id, double longtitude, double latitude) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = new JsonWriter( stringWriter );

        writer.beginObject()
                .name("type").value("location")
                .name("id").value(id)
                .name("longitude").value(""+longtitude)
                .name("latitude").value(""+latitude)
                .endObject();
        return new Expression(stringWriter.toString());
    }



}

