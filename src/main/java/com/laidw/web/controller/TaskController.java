package com.laidw.web.controller;

import com.laidw.entity.Membership;
import com.laidw.entity.Task;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.entity.to.TasksTO;
import com.laidw.service.MembershipService;
import com.laidw.service.TaskService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private GroupController groupController;

    @Autowired
    private MembershipService membershipService;

    @GetMapping
    public ModelAndView toCreateTaskPage(Integer gid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_INFO);
        mav.addObject("gid", gid);
        return mav;
    }

    @PostMapping
    public ModelAndView doCreateTask(Integer gid, String title, String content){
        taskService.insertTask(gid, title, content);
        ModelAndView mav= groupController.toGroupTalkPage(gid, true);
        mav.addObject("msg", "Created task successfully!");
        mav.addObject("goToTasks", "true");
        return mav;
    }

    @GetMapping("/{tid}")
    public ModelAndView toUpdateTaskPage(@PathVariable Integer tid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_INFO);
        Task task = taskService.selectTaskById(tid);
        Membership membership = membershipService.selectMembership(getCurrentUser().getId(), task.getGroupId(), true);

        //如果当前用户不是管理员，那肯定不能修改
        boolean isNormal = membership.getRole().equals(Role.NORMAL);

        //如果该任务是叶子任务，则它必须未被接收或者接收者是本人才能修改
        boolean sonCondition = !task.getHasSon() && (task.getAcceptorPubInfo() == null || (task.getAcceptorPubInfo().getId().equals(membership.getId()) && task.getFinTime() == null));

        //如果该任务是父任务，则它必须未完成才能修改
        boolean fatherCondition = task.getHasSon() && task.getProgress() != 1.0;

        if(!(!isNormal && (sonCondition || fatherCondition)))
            mav.addObject("readOnly", true);
        mav.addObject("task", task);
        return mav;
    }

    @PutMapping
    public ModelAndView doUpdateTask(Task task){
        taskService.updateTaskByIdSelectively(task);
        task = taskService.selectTaskById(task.getId());
        ModelAndView mav = groupController.toGroupTalkPage(task.getGroupId(), true);
        mav.addObject("msg", "Modified successfully!");
        mav.addObject("goToTasks", "true");
        return mav;
    }

    @PutMapping("/{opt}")
    public ModelAndView doOptTask(@PathVariable String opt, @RequestParam(required=false) Integer gid, @RequestParam(required=false) Integer mid, @RequestParam(required=false) Integer uid, Integer tid, @RequestParam(required=false) String result){
        switch (opt){
            case "acc" : taskService.acceptTask(mid, uid, tid); break;
            case "del" : taskService.giveUpTask(tid); break;
            case "fin" : taskService.finishTask(tid, result); break;
            default : throw new RuntimeException("Unsupported option type: " + opt);
        }
        //如果用户传来了gid，说明是在群聊页面操作的任务；否则则是在我接收的任务页面操作，需要回到相应的页面
        ModelAndView mav = gid == null ? toMyAcceptedTasksPage() : groupController.toGroupTalkPage(gid, true);
        mav.addObject("msg", "Option: " + opt + " was done successfully!");
        mav.addObject("goToTasks", "true");
        return mav;
    }

    @PutMapping("/split")
    public ModelAndView doSplitTask(Integer fid, Integer gid, TasksTO tasksTO){
        taskService.splitTask(fid, gid, tasksTO.getTasks());
        ModelAndView mav = groupController.toGroupTalkPage(gid, true);
        mav.addObject("msg", "Split successfully!");
        mav.addObject("goToTasks", "true");
        return mav;
    }

    @GetMapping("/my")
    public ModelAndView toMyAcceptedTasksPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_LIST);
        mav.addObject("tasks", taskService.selectTasksByAccUserId(getCurrentUser().getId()));
        return mav;
    }

    @DeleteMapping
    @ResponseBody
    public String doDeleteTask(Integer tid){
        Task task = taskService.selectTaskById(tid);
        Membership membership = membershipService.selectMembership(getCurrentUser().getId(), task.getGroupId(), true);
        if(membership.getRole().equals(Role.NORMAL))
            return "You have no right to delete this task!";
        if(task.getHasSon())
            return "Could not delete a father task!";
        if(task.getAcceptorPubInfo() != null)
            return "This task has been accepted!";
        taskService.deleteTaskById(tid);
        return "OK";
    }
}
