package com.example.attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
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


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION );
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

}

