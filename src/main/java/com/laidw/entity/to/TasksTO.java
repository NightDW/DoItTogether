package com.laidw.entity.to;

import com.laidw.entity.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 这是一个专门用于传输数据的对象
 * 用于让SpringMVC批量接收网页传来的Task信息
 */
@Getter@Setter
public class TasksTO {
    private List<Task> tasks;
}
