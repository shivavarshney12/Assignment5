package com.example.assignment5;


import androidx.room.Ignore;
        import androidx.room.ColumnInfo;
        import androidx.room.PrimaryKey;
        import androidx.room.Entity;

@Entity(tableName="trainingdata")
public class TrainingData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "ap1")
    public float ap1;
    @ColumnInfo(name = "ap2")
    public float ap2;
    @ColumnInfo(name = "ap3")
    public float ap3;
    @ColumnInfo(name = "loc")
    public String loc;
    public TrainingData(int id,float ap1,float ap2,float ap3,String loc){
        this.id=id;
        this.ap1=ap1;
        this.ap2=ap2;
        this.ap3=ap3;
        this.loc=loc;
    }
    @Ignore
    public TrainingData(float ap1,float ap2,float ap3,String loc){
        this.ap1=ap1;
        this.ap2=ap2;
        this.ap3=ap3;
        this.loc=loc;
    }
}