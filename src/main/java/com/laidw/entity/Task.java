package com.laidw.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用于存储任务的信息
 */
@Getter@Setter
@NoArgsConstructor
public class Task {

    /**
     * 任务的id
     */
    private Integer id;

    /**
     * 任务的发布时间
     */
    private Date pubTime;

    /**
     * 任务的完成时间，该字段可作为判断任务是否已完成的依据
     */
    private Date finTime;

    /**
     * 任务的标题/概述
     */
    private String title;

    /**
     * 任务的详细描述
     */
    private String content;

    /**
     * 接受者在完成任务后可以提交额外的信息
     * 该字段用于保存这个信息，方便其它成员查看完成结果
     */
    private String result;

    /**
     * 任务所在群的id，由于本群内的任务只有在本群内可见，因此保存群id方便查找即可
     */
    private Integer groupId;

    /**
     * 任务的接收者，只保存其中的非隐私信息，即不含isTop和isShow信息
     */
    private Membership acceptorPubInfo;

    /**
     * 任务的父任务id，如果该字段为空，说明该任务是根任务
     * 查询到根任务后，取出其id，然后根据fatherId = id条件继续查找叶子任务
     * 这里不能用Task father字段表示父任务，避免循环重复查找
     */
    private Integer fatherId;

    /**
     * 任务是否有子任务
     */
    private Boolean hasSon;

    /**
     * 任务的子任务
     */
    private List<Task> sons;

    /**
     * 获取该任务的完成进度
     * @return 该任务的完成进度
     */
    public double getProgress(){

        //如果本身是叶子任务，则进度只可以是0或100%
        if(!hasSon)
            return finTime == null ? 0 : 1;

        //如果不是叶子任务，那么其所有子任务的平均进度即为该任务的进度
        double count = 0;
        for(Task son: sons)
            count += son.getProgress();
        return count / sons.size();
    }

    /**
     * 获取该任务的状态
     * @return 该任务的状态
     */
    public String getStatus(){
        if(hasSon)
            return getProgress() == 1.0 ? "Sons done" : "Sons doing";
        if(finTime != null)
            return "Done";
        if(acceptorPubInfo != null)
            return "Doing";

        //Waiting表示该任务正等待被接收
        return "Waiting";
    }

    /**
     * 获取该任务的状态颜色，红色表示已完成，绿色表示进行中，蓝色表示未开始
     * @return 该任务的状态颜色
     */
    public String getStatusColor(){
        if(hasSon)
            return getProgress() == 1.0 ? "red" : "green";
        if(finTime != null)
            return "red";
        if(acceptorPubInfo != null)
            return "green";
        return "blue";
    }

    public Task(Integer id){
        this.id = id;
    }
}
