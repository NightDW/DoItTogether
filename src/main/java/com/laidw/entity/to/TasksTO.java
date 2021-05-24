package com.laidw.entity.to;

import com.laidw.entity.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 这是一个专门用于传输Task数据的对象
 * 用于让SpringMVC一次性接收网页传来的若干个Task信息
 */
@Getter@Setter
public class TasksTO {
    private List<Task> tasks;
}
