package com.example.quwanhuigou.bean;

/**
 * 任务实体类
 */
public class Task {

  private String taskNo;
  private String startTime;
  private String goodsName;
  private Double commissionBuyers;



  public Task(){}

  public Task(String taskNo, String startTime, String goodsName, Double commissionBuyers) {
    this.taskNo = taskNo;
    this.startTime = startTime;
    this.goodsName = goodsName;
    this.commissionBuyers = commissionBuyers;
  }

  public String getTaskNo() {
    return taskNo;
  }

  public void setTaskNo(String taskNo) {
    this.taskNo = taskNo;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getGoodsName() {
    return goodsName;
  }

  public void setGoodsName(String goodsName) {
    this.goodsName = goodsName;
  }

  public Double getCommissionBuyers() {
    return commissionBuyers;
  }

  public void setCommissionBuyers(Double commissionBuyers) {
    this.commissionBuyers = commissionBuyers;
  }
}