package com.intetics.organizerbot.entities.auto;

import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import com.intetics.organizerbot.entities.Subject;

/**
 * Class _Users was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Users extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String USER_ID_PK_COLUMN = "USER_ID";

    public static final Property<Integer> ID = Property.create("id", Integer.class);
    public static final Property<String> INFO = Property.create("info", String.class);
    public static final Property<String> USER_NAME = Property.create("userName", String.class);
    public static final Property<List<Subject>> SUBJECTS = Property.create("subjects", List.class);

    public void setId(int id) {
        writeProperty("id", id);
    }
    public int getId() {
        Object value = readProperty("id");
        return (value != null) ? (Integer) value : 0;
    }

    public void setInfo(String info) {
        writeProperty("info", info);
    }
    public String getInfo() {
        return (String)readProperty("info");
    }

    public void setUserName(String userName) {
        writeProperty("userName", userName);
    }
    public String getUserName() {
        return (String)readProperty("userName");
    }

    public void addToSubjects(Subject obj) {
        addToManyTarget("subjects", obj, true);
    }
    public void removeFromSubjects(Subject obj) {
        removeToManyTarget("subjects", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Subject> getSubjects() {
        return (List<Subject>)readProperty("subjects");
    }


}