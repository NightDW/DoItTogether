package com.laidw.entity.to;

import com.laidw.entity.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用于批量接收网页传来的Task信息
 */
@Getter@Setter
public class TasksTO {
    private List<Task> tasks;
}
