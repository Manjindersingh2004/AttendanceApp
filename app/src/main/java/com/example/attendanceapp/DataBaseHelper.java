package com.example.attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.codec.language.bm.Languages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BackupRefference="BACKUPS";
    private static final String USER_NAME="MSsandhu2004";
    private static final String BACKUP_FILE="backup.db";
    private static final String DB_NAME="student_attendance";
    private static final int DB_VERSION=1;
    private static final String STUDENT_TABLE ="students";
    private static final String GROUP_TABLE ="groups";
    private static final String COL_ROLLNO="roll_no";

    private static final String COL_NAME="name";
    private static final String COL_GROUP="group_";
    private static final String COL_ATEND_LEC ="atend_lecture";
    private static final String COL_TOTAL_LEC="total_lecture";
    private static final String COL_PERCENTAGE="percentage";
    private static final String ATTENDANCE_TABLE_PREFIX="attendance_";
    private static final String ATTENDANCE_TABLE_COL_PREFIX="student_";
    private static final String COL_DATE="attendance_date";
    Context context;


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION );
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query1 = "CREATE TABLE " + STUDENT_TABLE + " ("
                + COL_ROLLNO + " INTEGER , "
                + COL_NAME + " TEXT,"
                + COL_GROUP + " TEXT,"
                + COL_ATEND_LEC + " INTEGER,"
                + COL_TOTAL_LEC + " INTEGER,"
                + COL_PERCENTAGE + " INTEGER)";
        db.execSQL(query1);
        String query2 = "CREATE TABLE " + GROUP_TABLE + " ("
                + COL_GROUP + " TEXT PRIMARY KEY, "
                + COL_TOTAL_LEC + " INTEGER)";
        db.execSQL(query2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STUDENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        onCreate(db);

    }
    public void addNewStudents(String rollno, String name, String group,String atendlec,String totallec, String percentage) {

        String  total_lec=getTotalLectureOfGroup(group);
        String percentage_;
        if(total_lec.equals("0"))
            percentage_="0";
        else
            percentage_=(Integer.parseInt(atendlec)*100)/Integer.parseInt(total_lec)+"";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROLLNO, Integer.parseInt(rollno));
        values.put(COL_NAME, name.toUpperCase());
        values.put(COL_GROUP, group.toUpperCase());
        values.put(COL_ATEND_LEC, Integer.parseInt(atendlec));
        values.put(COL_TOTAL_LEC, Integer.parseInt(total_lec));
        values.put(COL_PERCENTAGE, Integer.parseInt(percentage_));
        db.insert(STUDENT_TABLE, null, values);
        db.close();
        addRollNoColumnToAtendanceTable(rollno,group);
    }

    public void removeStudent(String rollno,String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STUDENT_TABLE, COL_ROLLNO+"="+rollno+" and "+COL_GROUP+"='"+group+"'",null);
        db.close();
        removeColumnFromAttendanceTable(rollno,group);
    }
    // below is the method for updating our courses
    public void updateStudentData(String key,String rollno, String name, String group,
                                  String atendlec,String totallec) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(rollno.length()>0 && !rollno.equals(key)){
            ContentValues v = new ContentValues();
            v.put(COL_ROLLNO, Integer.parseInt(rollno));
            db.update(STUDENT_TABLE, v, COL_ROLLNO+"="+key+" and "+COL_GROUP+"='"+group+"'",null);
            renameRollNoCulumnToAttendanceTable(key,rollno,group);
            db = this.getWritableDatabase();
            key=rollno;
        }
        if(name.length()>0)
            values.put(COL_NAME, name.toUpperCase());
//        String Exgroup="";
//        String quary="SELECT "+COL_GROUP+" FROM "+STUDENT_TABLE+" WHERE "+COL_ROLLNO+" ="+Integer.parseInt(key);
//        Cursor c= db.rawQuery(quary,null);
//        if(c.moveToFirst())
//            Exgroup=c.getString(0);
//        if(group.length()>0 && !Exgroup.equals(group)){
//            values.put(COL_GROUP, group.toUpperCase());
//            String total_lec=getTotalLectureOfGroup(group);
//            values.put(COL_TOTAL_LEC,total_lec);
//            values.put(COL_ATEND_LEC,"0");
//            values.put(COL_PERCENTAGE,"0");
//        }
        db.update(STUDENT_TABLE, values, COL_ROLLNO+"="+key+" and "+COL_GROUP+"='"+group+"'", null);
        db.close();

//        if(!Exgroup.equals(group)){
//            changeGroupOfRollColumnFromAttendanceTable(Exgroup,rollno,group);
//        }
    }

    void updatePercentage(String key,String group){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("UPDATE " + STUDENT_TABLE +
                " SET " + COL_PERCENTAGE + " = (" + COL_ATEND_LEC + " * 100) /"+COL_TOTAL_LEC +
                " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(key)+" and "+COL_GROUP+"='"+group+"'");
        db.close();
    }

    // student table attendance all data
    void incrementAttendanceStudentTable(ArrayList<StudentDataModel> arrayList, ArrayList<String> attendance){
        String key;
        String group=arrayList.get(0).GROUP;
        incrementAttendanceGroupTable(group);
        for(int i=0;i<arrayList.size();i++){
            SQLiteDatabase db= this.getWritableDatabase();
            key=arrayList.get(i).ROLL_NO;
            if(attendance.get(i).equals("1")){
                db.execSQL("UPDATE " + STUDENT_TABLE +
                        " SET " + COL_ATEND_LEC + " = " + COL_ATEND_LEC + " +1 "+
                        " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(key)+" and "+COL_GROUP+"='"+group+"'");
            }
            db.execSQL("UPDATE " + STUDENT_TABLE +
                    " SET " + COL_TOTAL_LEC + " = " + COL_TOTAL_LEC + " +1 "+
                    " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(key)+" and "+COL_GROUP+"='"+group+"'");
            updatePercentage(key,group);
        }

    }

    void decrementAttendanceStudentTable(ArrayList<StudentDataModel> arrayList, ArrayList<String> attendance){
        String key;
        String group=arrayList.get(0).GROUP;
        decrementAttendanceGroupTable(group);
        for(int i=0;i<arrayList.size();i++){
            SQLiteDatabase db= this.getWritableDatabase();
            key=arrayList.get(i).ROLL_NO;
            if(attendance.get(i).equals("1")){
                db.execSQL("UPDATE " + STUDENT_TABLE +
                        " SET " + COL_ATEND_LEC + " = " + COL_ATEND_LEC + " -1 "+
                        " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(key)+" and "+COL_GROUP+"='"+group+"'");
            }
            db.execSQL("UPDATE " + STUDENT_TABLE +
                    " SET " + COL_TOTAL_LEC + " = " + COL_TOTAL_LEC + " -1 "+
                    " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(key)+" and "+COL_GROUP+"='"+group+"'");
            updatePercentage(key,group);


        }

    }

    ArrayList<StudentDataModel> fetchGroupData(String key){
        ArrayList<StudentDataModel> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT * FROM "+ STUDENT_TABLE +" WHERE "+COL_GROUP+"='"+key+"' ORDER BY "+COL_ROLLNO;
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                arrayList.add(new StudentDataModel(c.getString(0),c.getString(1),c.getString(2),String.valueOf(c.getInt(3)),String.valueOf(c.getInt(4)),String.valueOf(c.getInt(5))));
            }
            while(c.moveToNext());
        }
        return arrayList;
    }

    ArrayList<StudentDataModel> fetchDetainedStudentData(){
        ArrayList<StudentDataModel> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT * FROM "+ STUDENT_TABLE +" WHERE "+COL_PERCENTAGE+"<75"+" ORDER BY "+COL_GROUP+","+COL_ROLLNO;
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                arrayList.add(new StudentDataModel(c.getString(0),c.getString(1),c.getString(2),String.valueOf(c.getInt(3)),String.valueOf(c.getInt(4)),String.valueOf(c.getInt(5))));
            }
            while(c.moveToNext());
        }
        return arrayList;
    }

    public void reSetAttendance(String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ATEND_LEC,0);
        values.put(COL_TOTAL_LEC,0);
        values.put(COL_PERCENTAGE,0);
        db.update(STUDENT_TABLE, values,COL_GROUP+" ='"+group+"'",null);
        ContentValues v = new ContentValues();
        v.put(COL_TOTAL_LEC,0);
        db.update(GROUP_TABLE,v ,COL_GROUP+" ='"+group+"'",null);

        db.close();
    }
    Integer checkRollnoExists(String rollno,String group){
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT "+COL_ROLLNO+" FROM "+STUDENT_TABLE+" WHERE "+COL_ROLLNO+" ="+rollno+" and "+COL_GROUP+"='"+group+"'";
        Cursor c= db.rawQuery(quary,null);
        if(c.moveToFirst())
            return 1;
        else
            return 0;
    }
//    String getGroupName(String rollno){
//        String group="";
//        SQLiteDatabase db0=this.getReadableDatabase();
//        String quary="SELECT "+COL_GROUP+" FROM "+STUDENT_TABLE+" WHERE "+COL_ROLLNO+" ="+rollno;
//        Cursor c= db0.rawQuery(quary,null);
//        if(c.moveToFirst())
//            group=c.getString(0);
//        db0.close();
//        return  group;
//    }

    Integer checkDetainedStudentExist(){
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT "+COL_ROLLNO+" FROM "+STUDENT_TABLE +" WHERE "+COL_PERCENTAGE+" <"+75;
        Cursor c= db.rawQuery(quary,null);
        if(c.moveToFirst())
            return 1;
        else
            return 0;
    }


    //--------------------------------------------------------------------------------------------------------------------------
    void addGroupToGroupTable(String group,String total_lec){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_GROUP,group.toUpperCase());
        values.put(COL_TOTAL_LEC,Integer.parseInt(total_lec));
        db.insert(GROUP_TABLE,null,values);
        db.close();

        createAttendanceTableForGroup(group);
    }
    void removeGroup(String group){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GROUP_TABLE, COL_GROUP+"=?", new String[]{group});
        db.delete(STUDENT_TABLE, COL_GROUP+"=?", new String[]{group});
        db.close();
        deleteAttendanceTableForGroup(group.toUpperCase());
    }

    ArrayList<String> fetchGroupTable(){
        ArrayList<String> arrayList=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT "+COL_GROUP+" FROM "+ GROUP_TABLE +" ORDER BY "+COL_GROUP;
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                arrayList.add(c.getString(0));
            }
            while(c.moveToNext());
        }
        return arrayList;
    }

    public void renameGroup(String key,String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(group.length()>0)
            values.put(COL_GROUP, group.toUpperCase());
        db.update(GROUP_TABLE, values, COL_GROUP+"=?", new String[]{key});
        db.update(STUDENT_TABLE, values, COL_GROUP+"=?", new String[]{key});
        db.close();

        renameAttendanceTableForGroup(key.toUpperCase(),group.toUpperCase());
    }
    String getTotalLectureOfGroup(String group) {
        SQLiteDatabase db=this.getReadableDatabase();
        String total_lec="";
        String quary="SELECT "+COL_TOTAL_LEC+" FROM "+GROUP_TABLE+" WHERE "+COL_GROUP+"='"+group+"'";
        Cursor c= db.rawQuery(quary,null);
        if(c.moveToFirst()){
            total_lec=c.getString(0).toString();
        }
        return total_lec;
    }
    void incrementAttendanceGroupTable(String group) {
        SQLiteDatabase db0= this.getWritableDatabase();
        db0.execSQL("UPDATE " + GROUP_TABLE +
                " SET " + COL_TOTAL_LEC + " = " + COL_TOTAL_LEC + " +1 "+
                " WHERE "+COL_GROUP+" = '"+group+"'");
        db0.close();
    }
    void decrementAttendanceGroupTable(String group) {
        SQLiteDatabase db0= this.getWritableDatabase();
        db0.execSQL("UPDATE " + GROUP_TABLE +
                " SET " + COL_TOTAL_LEC + " = " + COL_TOTAL_LEC + " -1 "+
                " WHERE "+COL_GROUP+" = '"+group+"'");
        db0.close();
    }

    Integer checkGroupExists(String group){
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT "+COL_GROUP+" FROM "+GROUP_TABLE+" WHERE "+COL_GROUP+" ='"+group.toUpperCase()+"'";
        Cursor c= db.rawQuery(quary,null);
        if(c.moveToFirst())
            return 1;
        else
            return 0;
    }


    //----------------------------------------------------------------------------------------------------------------
    Integer checkDataExists(String table, String group){
        String Table="";
        if(table.equals("1"))
            Table=STUDENT_TABLE;
        else
            Table=GROUP_TABLE;
        if(group.equals(" "))
            group=COL_GROUP;
        else
            group="'"+group+"'";
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="SELECT "+COL_GROUP+" FROM "+Table +" WHERE "+COL_GROUP+" ="+group;
        Cursor c= db.rawQuery(quary,null);
        if(c.moveToFirst())
            return 1;
        else
            return 0;
    }
//-----------------------------------------------------------------------------------------------------
    void createAttendanceTableForGroup(String group){
        String Table_name=getAtendanceTableName(group);
        String Quary="CREATE TABLE "+Table_name+" ( "+COL_DATE+" TEXT )";

        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(Quary);
        db.close();
    }

    void deleteAttendanceTableForGroup(String group){
        String Table_name=getAtendanceTableName(group);
        String Quary="DROP TABLE IF EXISTS " + Table_name;

        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(Quary);
        db.close();
    }

    void renameAttendanceTableForGroup(String exgroup,String newgroup){
        String Table_name=getAtendanceTableName(exgroup);
        String New_Table_name=getAtendanceTableName(newgroup);
        String Quary="ALTER TABLE "+Table_name+" RENAME TO "+New_Table_name;

        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(Quary);
        db.close();
    }

    void addRollNoColumnToAtendanceTable(String rollno,String group){
        String Column_name=getRollnoColumnNameAtendanceTable(rollno);
        //String group=getGroupName(rollno);
        String Table_name=getAtendanceTableName(group);
        String Quary= "ALTER TABLE " + Table_name + " ADD COLUMN " + Column_name+ " TEXT";

        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(Quary);
        db.close();
    }

    void removeColumnFromAttendanceTable(String rollno,String group) {
        //String group = getGroupName(rollno); // but not exists ex rollno so use new rollno // then check group change and update
        String tableName = getAtendanceTableName(group);
        String columnToRemove = getRollnoColumnNameAtendanceTable(rollno).toLowerCase();
        // Create a new table without the column
        String createNewTableQuery = "CREATE TABLE new_table AS SELECT ";
        String separator = "";
        List<String> existingColumns = getExistingColumnNamesFromAttendanceTable(tableName);

        for (String column : existingColumns) {
            if (!column.equals(columnToRemove)) {
                createNewTableQuery += separator + column;
                separator = ", ";
            }
        }
        createNewTableQuery += " FROM " + tableName;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(createNewTableQuery);

        // Drop the old table
        String dropOldTableQuery = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(dropOldTableQuery);

        // Rename the new table to the original table name if needed
        String renameTableQuery = "ALTER TABLE new_table RENAME TO " + tableName;
        db.execSQL(renameTableQuery);

        db.close();
    }


    void renameRollNoCulumnToAttendanceTable(String exrollno, String newRollno,String group) {
        //String group = getGroupName(newRollno); // but not exists ex rollno so use new rollno // then check group change and update
        String tableName = getAtendanceTableName(group);
        String oldColumnName = getRollnoColumnNameAtendanceTable(exrollno).toLowerCase();
        String newColumnName = getRollnoColumnNameAtendanceTable(newRollno).toLowerCase();

        String createNewTableQuery = "CREATE TABLE new_table AS SELECT ";
        String separator = "";
        List<String> existingColumns = getExistingColumnNamesFromAttendanceTable(tableName);

        for (String column : existingColumns) {
            if (!column.equals(oldColumnName)) {
                createNewTableQuery += separator + column;
            } else {
                createNewTableQuery += separator + column + " AS " + newColumnName;
            }
            separator = ", ";
        }

        createNewTableQuery += " FROM " + tableName;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(createNewTableQuery);

        // Drop the old table
        String dropOldTableQuery = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(dropOldTableQuery);

        // Rename the new table to the original table name if needed
        String renameTableQuery = "ALTER TABLE new_table RENAME TO " + tableName;
        db.execSQL(renameTableQuery);

        db.close();
    }

    void changeGroupOfRollColumnFromAttendanceTable(String exgroup,String rollno,String newGroup) {
        removeColumnFromAttendanceTable(rollno,exgroup);
        addRollNoColumnToAtendanceTable(rollno,newGroup);
    }

    //----------------------------------------------------------------------------------------------

    void entryInAttendanceTable(ArrayList<StudentDataModel> arrayList, ArrayList<String> attendance,String date){
        String key,attend;
        String group=arrayList.get(0).GROUP;
        String tableName = getAtendanceTableName(group);
        ContentValues values=new ContentValues();
        values.put(COL_DATE,date);
        for(int i=0;i<arrayList.size();i++){
            key=arrayList.get(i).ROLL_NO;
            attend=attendance.get(i);
            if(attend.equals("1")) {
                values.put(getRollnoColumnNameAtendanceTable(key),"Present");
            }
            else{
                values.put(getRollnoColumnNameAtendanceTable(key),"Absent");
            }
        }
        SQLiteDatabase db=this.getWritableDatabase();
        db.insert(tableName,null,values);
        db.close();

    }


    ArrayList<String> getAttendanceListFromAttendanceTableByDate(String date,String group,ArrayList<StudentDataModel> arrayList){

        String Table=getAtendanceTableName(group);
        String column;
        ArrayList<String> attendance=new ArrayList<>();
        for(int i=0;i<arrayList.size();i++){
            column=getRollnoColumnNameAtendanceTable(arrayList.get(i).ROLL_NO);
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor c=db.rawQuery("SELECT "+column+" FROM "+Table+" WHERE "+COL_DATE+" ='"+date+"'",null,null);
            if(c.moveToFirst()){
                String value=c.getString(0);
                if(value==null)
                    value="Absent";
                if(value.equals("Present"))
                attendance.add("1");
                else attendance.add("0");
            }
            db.close();
        }
        return attendance;
    }

    void deleteRowByDateInAttendanceTable(String date,String group){
        String Table=getAtendanceTableName(group);
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Table,COL_DATE+"='"+date+"'",null);
        db.close();
    }



    //------------------------------------------------------------------------------------------------
    String replaceSpacesWithUnderscores(String input) {
        if (input == null) {
            return null;
        }
        return input.replace(" ", "_");
    }

    String getAtendanceTableName(String group){
        return ATTENDANCE_TABLE_PREFIX+replaceSpacesWithUnderscores(group);
    }

    String getRollnoColumnNameAtendanceTable(String rollno){
        return ATTENDANCE_TABLE_COL_PREFIX+rollno;
    }

    List<String> getExistingColumnNamesFromAttendanceTable(String tableName) {
        List<String> columnNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query for the table's schema
        String query = "PRAGMA table_info(" + tableName + ")";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String columnName = cursor.getString(cursor.getColumnIndex("name"));
                    columnNames.add(columnName.toLowerCase());

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        db.close();
        return columnNames;
    }

    Integer checkDateExist(String date,String group){
        String Table=getAtendanceTableName(group);
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor C=db.rawQuery(" SELECT * FROM "+Table+" WHERE "+COL_DATE+"='"+date+"'",null,null);
        if(C.moveToFirst()){
            return 1;
        }
        else {
            return 0;
        }
    }

    void deleteAttendanceRows(String group){
        String Table=getAtendanceTableName(group);
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(Table,null,null);
        db.close();
    }


    void updateStudentTableAttendanceRetake(ArrayList<StudentDataModel>arrayList ,ArrayList<String> exAttendance,ArrayList<String> newAttendance){
        String rollno,exAtend,newAtend,group;
        group=arrayList.get(0).GROUP.toString();
        for(int i=0;i<arrayList.size();i++){
            SQLiteDatabase db=this.getWritableDatabase();
            rollno=arrayList.get(i).ROLL_NO.toString();
            exAtend=exAttendance.get(i);
            newAtend=newAttendance.get(i);
            if(exAtend.equals("1") && newAtend.equals("0")) {
                //--
                db.execSQL("UPDATE " + STUDENT_TABLE +
                        " SET " + COL_ATEND_LEC + " = " + COL_ATEND_LEC + " -1 "+
                        " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(rollno)+" and "+COL_GROUP+"='"+group+"'");
            } else if (exAtend.equals("0")&& newAtend.equals("1")) {
                //++
                db.execSQL("UPDATE " + STUDENT_TABLE +
                        " SET " + COL_ATEND_LEC + " = " + COL_ATEND_LEC + " +1 "+
                        " WHERE "+COL_ROLLNO+" = "+Integer.parseInt(rollno)+" and "+COL_GROUP+"='"+group+"'");
            }
            updatePercentage(rollno,group);
            db.close();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------

    ArrayList<String> getDatesList(String group){
        String Table=getAtendanceTableName(group);
        ArrayList<String> date=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT "+COL_DATE+" FROM "+Table+" ORDER BY "+COL_DATE,null,null);
        if(c.moveToFirst()){
            do{
                String inputDate = c.getString(0); // Replace with your input date

// Split the input date into year, month, and day parts
                String[] parts = inputDate.split("-");
                if (parts.length == 3) {
                    String year = parts[0];
                    String month = parts[1];
                    String day = parts[2];
                    int month_num=Integer.parseInt(month);
                    switch (month_num){
                        case 1:month=" Jan,";
                        break;
                        case 2:month=" Feb,";
                            break;
                        case 3:month=" Mar,";
                            break;
                        case 4:month=" Apr,";
                            break;
                        case 5:month=" May,";
                            break;
                        case 6:month=" June";
                            break;
                        case 7:month=" Jul,";
                            break;
                        case 8:month=" Aug,";
                            break;
                        case 9:month=" Sep,";
                            break;
                        case 10:month=" Oct,";
                            break;
                        case 11:month=" Nov,";
                            break;
                        case 12:month=" Dec,";
                            break;
                        default: month=" Invalid,";
                    }
                    // Reformat the date in "DD-MM-YYYY" format
                    String formattedDate = day + month +  year;
                    date.add(formattedDate);
                }

            }while (c.moveToNext());
        }
        return date;
    }

    ArrayList<String> getAttendanceAttendanceTAbleOfRollNo(String group,String rollno){
        String Table=getAtendanceTableName(group);
        String Column=getRollnoColumnNameAtendanceTable(rollno);
        ArrayList<String> attendance=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT "+Column+" FROM "+Table+" ORDER BY "+COL_DATE,null,null);
        if(c.moveToFirst()){
            do{
                if(c.getString(0)==null)
                    attendance.add("Absent");
                else
                attendance.add(c.getString(0));
            }while (c.moveToNext());
        }
        db.close();
        return  attendance;
    }



    //-----------------------------------------------------------------------------------------------------------------------------------------
    ArrayList<Integer> getAverageGroupAttendance(String group){
        ArrayList<Integer> progressData=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        int students=0;
        int sum=0;
        int percentage=0;
        int lecture=0;
        String quary="SELECT "+COL_PERCENTAGE+" FROM "+ STUDENT_TABLE +" WHERE "+COL_GROUP+"='"+group+"'";
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                students++;
                if(c.getString(0)!=null)
                sum+=Integer.parseInt(c.getString(0));
            }
            while(c.moveToNext());
        }
        String quary2="SELECT "+COL_TOTAL_LEC+" FROM "+ GROUP_TABLE +" WHERE "+COL_GROUP+"='"+group+"'";
        c=db.rawQuery(quary2,null);
        if(c.moveToFirst()){
           // if(c.getString(0)!=null)
            lecture=Integer.parseInt(c.getString(0));
        }
        if(students!=0)
            percentage=sum/students;

        progressData.add(students);
        progressData.add(lecture);
        progressData.add(percentage);
        return progressData;
    }










//--------------------------------------Backup-------------------------------------------------------------------------------


    public void exportDatabase(Context context) {
        try {
            File dataDirectory = Environment.getDataDirectory();
            String currentDBPath = context.getDatabasePath(DB_NAME).getAbsolutePath();
            //String backupDBPath = Environment.getExternalStorageDirectory() + File.separator + BACKUP_FILE;
            String backupDBPath = context.getCacheDir() + File.separator + BACKUP_FILE;

            File currentDB = new File(currentDBPath);
            File backupDB = new File(backupDBPath);

            if (currentDB.exists()) {
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                FileChannel src = fis.getChannel();
                FileChannel dst = fos.getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                fis.close();
                fos.close();

                // Now, you have a backup of the database at backupDBPath
                Toast.makeText(context, "Backup done", Toast.LENGTH_SHORT).show();
                uploadBackup(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Backup error", Toast.LENGTH_SHORT).show();
        }
    }


    void uploadBackup(Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference backupRef = storageRef.child(BackupRefference).child(USER_NAME).child(BACKUP_FILE);
//        String backupDBPath = Environment.getExternalStorageDirectory() + File.separator + BACKUP_FILE;
        String backupDBPath = context.getCacheDir() + File.separator + BACKUP_FILE;

        Uri file = Uri.fromFile(new File(backupDBPath));
        UploadTask uploadTask = backupRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful upload
                // You can get the download URL if needed: taskSnapshot.getDownloadUrl()
                Toast.makeText(context, "backup upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed upload
                Toast.makeText(context, "backup upload failed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    void downloadBackup(Context context){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference backupRef = storageRef.child(BackupRefference).child(USER_NAME).child(BACKUP_FILE);
        File localFile = new File(context.getDatabasePath(BACKUP_FILE).getAbsolutePath());

        backupRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Database backup downloaded successfully
                // Now you can proceed to restore the database
                Toast.makeText(context, "Backup Download ", Toast.LENGTH_SHORT).show();
                // You may want to notify the user or perform any additional actions
                storeBackup(context);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                Toast.makeText(context, "Backup Download failed", Toast.LENGTH_SHORT).show();

            }
        });
    }
    void storeBackup(Context context){
        try {
            String currentDBPath = context.getDatabasePath(DB_NAME).getAbsolutePath();
            File currentDB = context.getDatabasePath(currentDBPath);
            File backupDB = new File(context.getDatabasePath(BACKUP_FILE).getAbsolutePath());

            if (backupDB.exists()) {
                FileInputStream fis = new FileInputStream(backupDB);
                FileOutputStream fos = new FileOutputStream(currentDB);
                FileChannel src = fis.getChannel();
                FileChannel dst = fos.getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                fis.close();
                fos.close();
                // Database restored successfully
                // You may want to notify the user or perform any additional actions
                Toast.makeText(context, "Backup Stored", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Backup Stored failed", Toast.LENGTH_SHORT).show();

        }
    }

    void uploadAttendance(String group){
        String Table=getAtendanceTableName(group);

    }



//    -------------------------------------------------------------------------------------------------------------------





    void insertAttendanceOnline(String group) {

        ArrayList<StudentDataModel>  arrayList=new ArrayList<>();
        ArrayList<ArrayList<String>> attendance=new ArrayList<>();
        ArrayList<String>  dates=new ArrayList<>();


        DataBaseHelper db=new DataBaseHelper(context);
        dates=db.getDatesList(group);
        arrayList=db.fetchGroupData(group);
        for(int i=0;i<arrayList.size();i++){
            String rollno=arrayList.get(i).ROLL_NO;
            ArrayList<String> row=new ArrayList<>();
            row=db.getAttendanceAttendanceTAbleOfRollNo(group,rollno);
            attendance.add(row);
        }


        String rollno,name,percentage_;

        for (int i = 0; i < dates.size(); i++) {

            String date_=dates.get(i);
        }

        for(int i=0;i<arrayList.size();i++){
            ArrayList<String> row_list=new ArrayList<>();
            row_list=attendance.get(i);
            rollno=arrayList.get(i).ROLL_NO;
            name=arrayList.get(i).NAME;
            percentage_=arrayList.get(i).PERCENTAGE;
            for (int j = 0; j < row_list.size(); j++) {
                String date_=dates.get(j);
                String attendance_;

                if(row_list.get(j)==null)
                    attendance_="Absent";
                else
                    attendance_=(row_list.get(j));
//                String quary="SELECT "+COL_PERCENTAGE+" FROM "+ STUDENT_TABLE +" WHERE "+COL_GROUP+"='"+group.toUpperCase()+"' and "+COL_ROLLNO+"="+rollno;
//                Cursor c=db.getReadableDatabase().rawQuery(quary, null);
//                if(c.moveToFirst()){
//
//                        if(c.getString(0)!=null)
//                            percentage=c.getString(0).toString();
//
//                }
                storeAttendanceIntoFirebase(rollno,name,group,date_,attendance_,percentage_);
            }
        }
    }


    String COLLAGES="COLLAGES";
    String COLLAGE_ID = "12345";


    String GROUPS = "GROUPS";
    String STUDENTS = "STUDENTS";
    String ATTENDANCE = "ATTENDANCE";
    String DATE="DATE";
    String NAME="NAME";
    String ROLLNO="ROLL_NO";
    String PERCENTAGE="PERCENTAGE";

    private void storeAttendanceIntoFirebase(String rollno, String name, String group, String date, String attendance,String percentage) {
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno);

        attendanceRef.child(NAME).setValue(name);
        attendanceRef.child(GROUPS).child(group).child(PERCENTAGE).setValue(percentage);
        attendanceRef.child(GROUPS).child(group).child(ATTENDANCE).child(date).setValue(attendance);
    }


    void resetAttendanceIntoFirebase(String group) {

        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select "+COL_ROLLNO+" from "+STUDENT_TABLE+" where "+COL_GROUP+"='"+group.toUpperCase()+"'";
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(c.getString(0)).child(GROUPS).child(group).removeValue();
            }
            while(c.moveToNext());
        }
    }


    void removeGroupIntoFirebase(String group) {

        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select "+COL_ROLLNO+" from "+STUDENT_TABLE+" where "+COL_GROUP+"='"+group.toUpperCase()+"'";
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                int count=countGroupsofRollnumbers(c.getString(0),group.toUpperCase());
                if(count==1){
                    FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(c.getString(0)).removeValue();
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(c.getString(0)).child(GROUPS).child(group).removeValue();
                }
            }
            while(c.moveToNext());
        }
    }

    void removeAttendanceDateIntoFirebase(String group,String date) {
        String[] parts = date.split("-");
        if (parts.length == 3) {
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            int month_num = Integer.parseInt(month);
            switch (month_num) {
                case 1:
                    month = " Jan,";
                    break;
                case 2:
                    month = " Feb,";
                    break;
                case 3:
                    month = " Mar,";
                    break;
                case 4:
                    month = " Apr,";
                    break;
                case 5:
                    month = " May,";
                    break;
                case 6:
                    month = " June";
                    break;
                case 7:
                    month = " Jul,";
                    break;
                case 8:
                    month = " Aug,";
                    break;
                case 9:
                    month = " Sep,";
                    break;
                case 10:
                    month = " Oct,";
                    break;
                case 11:
                    month = " Nov,";
                    break;
                case 12:
                    month = " Dec,";
                    break;
                default:
                    month = " Invalid,";
            }
            date = day + month +  year;
        }
        String rollno;
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select "+COL_ROLLNO+" from "+STUDENT_TABLE+" where "+COL_GROUP+"='"+group.toUpperCase()+"'";
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                rollno=c.getString(0);
                FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno).child(GROUPS).child(group).child(ATTENDANCE).child(date).removeValue();

                quary="SELECT "+COL_PERCENTAGE+" FROM "+ STUDENT_TABLE +" WHERE "+COL_GROUP+"='"+group.toUpperCase()+"' and "+COL_ROLLNO+"="+rollno;
                Cursor c2=db.rawQuery(quary,null);
                if(c2.moveToFirst()){
                    FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno).child(GROUPS).child(group).child(PERCENTAGE).setValue(c2.getString(0));
                }

            }
            while(c.moveToNext());
        }
    }

    void renameGroupAttendnceInFireBase(String key,String newgroup){

        String rollno;
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select "+COL_ROLLNO+" from "+STUDENT_TABLE+" where "+COL_GROUP+"='"+newgroup.toUpperCase()+"'";//because local database is updated already
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            do{
                rollno=c.getString(0);
                DatabaseReference oldGroup= FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno).child(GROUPS).child(key);
                DatabaseReference newGroup= FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno).child(GROUPS).child(newgroup);

                oldGroup.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //Toast.makeText(context, "rename Finished 1", Toast.LENGTH_SHORT).show();

                            Object data=snapshot.getValue();
                            newGroup.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "rename Finished 1", Toast.LENGTH_SHORT).show();

                                    oldGroup.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           // Toast.makeText(context, "rename Finished 2", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }else{
                            //Toast.makeText(context, "rename not Finished 1", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Toast.makeText(context, "rename error Finished 1", Toast.LENGTH_SHORT).show();

                    }
                });

            }
            while(c.moveToNext());
        }else{
            //Toast.makeText(context, "data not found", Toast.LENGTH_SHORT).show();
        }

    }


    void addStudentAttendanceFireBase(String rollno,String name,String group){
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno);
        attendanceRef.child(NAME).setValue(name.toUpperCase());
        attendanceRef.child(GROUPS).child(group.toUpperCase()).child(PERCENTAGE).setValue(0);
        attendanceRef.child(GROUPS).child(group.toUpperCase()).child(ATTENDANCE).setValue(null);
    }

    void removeStudentAttendanceFirebase(String rollno,String group,int count){
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(rollno);

        if(count==1){
            attendanceRef.removeValue();
        }
        else{
            attendanceRef.child(GROUPS).child(group.toUpperCase()).removeValue();
        }

    }

    void updateStudentsRollNoAttendanceFireBase(String key,String newrollno,String group,String name){
        //DatabaseReference oldRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(key);
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(newrollno);

        DatabaseReference oldRefGroup = FirebaseDatabase.getInstance().getReference().child(COLLAGES).child(COLLAGE_ID).child(ROLLNO).child(key).child(GROUPS).child(group.toUpperCase());


        oldRefGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Object data =snapshot.getValue();
                    oldRefGroup.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            newRef.child(GROUPS).child(group.toUpperCase()).setValue(data);
                            newRef.child(NAME).setValue(name.toUpperCase());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    int countRollnumbers(String rollno){
        int count=0;
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select count("+COL_ROLLNO+") from "+STUDENT_TABLE+" where "+COL_ROLLNO+"="+rollno;//because local database is updated already
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            count=c.getInt(0);
        }
        return count;
    }


    int countGroupsofRollnumbers(String rollno,String group){
        int count=0;
        SQLiteDatabase db=this.getReadableDatabase();
        String quary="select count("+COL_GROUP+") from "+STUDENT_TABLE+" where "+COL_ROLLNO+"="+rollno;//because local database is updated already
        Cursor c=db.rawQuery(quary,null);
        if(c.moveToFirst()){
            count=c.getInt(0);
        }
        return count;
    }


}

