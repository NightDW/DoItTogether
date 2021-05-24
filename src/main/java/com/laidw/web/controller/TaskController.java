package com.laidw.web.controller;

import com.laidw.entity.Membership;
import com.laidw.entity.Task;
import com.laidw.entity.common.Role;
import com.laidw.entity.to.TasksTO;
import com.laidw.service.MembershipService;
import com.laidw.service.TaskService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 负责任务相关的业务
 */
@Controller
@RequestMapping("/task")
public class TaskController{

    @Autowired
    private TaskService taskService;

    @Autowired
    private GroupController groupController;

    @Autowired
    private MembershipService membershipService;


    /**
     * 来到创建任务页面
     * @param gid 该任务所在群组的id
     * @return 任务编辑页面
     */
    @GetMapping
    public ModelAndView toCreateTaskPage(Integer gid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_INFO);
        mav.addObject("gid", gid);
        return mav;
    }

    /**
     * 处理创建任务的请求
     * @param gid 任务所在的群组的id
     * @param title 任务的标题
     * @param content 任务的内容
     * @return 回到群聊的详细信息页面，且直接打开tasks标签页
     */
    @PostMapping
    public ModelAndView doCreateTask(Integer gid, String title, String content){
        taskService.insertLeafTaskReturnId(gid, title, content);
        return groupController.toGroupTalkPage(gid, true, "Created task successfully!");
    }

    /**
     * 前往更新任务信息页面，只能修改任务的基本信息，并且需要根据权限确定是否允许修改
     * @param tid 任务的id
     * @return 任务编辑页面
     */
    @GetMapping("/{tid}")
    public ModelAndView toUpdateTaskPage(@PathVariable Integer tid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_INFO);
        Task task = taskService.selectFullTaskById(tid);
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), task.getGroupId(), true);

        //能修改该任务的条件：1. 用户是管理员；2. 任务是叶子任务；3. 任务没人接收
        //注意，即使接收者是自己且任务未完成，也不能修改，必须先放弃该任务
        boolean canModify = !membership.getRole().equals(Role.NORMAL) && !task.getHasSon() && task.getAcceptorPubInfo() == null;

        mav.addObject("readOnly", !canModify);
        mav.addObject("task", task);
        return mav;
    }

    /**
     * 处理用户修改任务的请求，只能修改基本信息
     * @param task 用户提交的数据
     * @return 回到群聊的详细信息页面，且直接打开tasks标签页
     */
    @PutMapping
    public ModelAndView doUpdateTask(Task task){

        //TODO 权限验证；但考虑到发送PUT请求比较麻烦，该方法很难被访问到，而且这里权限验证比较复杂，因此暂时不考虑

        taskService.updateTaskByIdSelectively(task);
        if(task.getGroupId() == null)
            task = taskService.selectTaskById(task.getId());
        return groupController.toGroupTalkPage(task.getGroupId(), true, "Modified successfully!");
    }

    /**
     * 负责处理用户接收/放弃/完成任务的请求
     * @param opt 可取："acc" "del" "fin"
     * @param gid 群组的id
     * @param mid 当前用户在该群组的Membership的id
     * @param uid 当前用户的id
     * @param tid 任务的id
     * @param result 完成任务时，可以额外提交的数据
     * @return 回到群聊的详细信息页面，且直接打开tasks标签页，或回到"我接收的任务"页面
     */
    @PutMapping("/{opt}")
    public ModelAndView doOptTask(@PathVariable String opt, @RequestParam(required=false) Integer gid, @RequestParam(required=false) Integer mid, @RequestParam(required=false) Integer uid, Integer tid, @RequestParam(required=false) String result){

        //TODO 权限验证，在接收任务时，应该判断任务是否已经被接收，等等；这里暂时忽略

        switch (opt){
            case "acc" : taskService.acceptTask(mid, uid, tid); break;
            case "del" : taskService.giveUpThisUnfinishedTask(tid); break;
            case "fin" : taskService.finishTask(tid, result); break;
            default : throw new RuntimeException("Unsupported option: " + opt);
        }

        //如果用户传来了gid，说明是在群聊页面操作的任务；否则则是在"我接收的任务"页面进行的操作
        ModelAndView mav = gid == null ? toMyAcceptedTasksPage() : groupController.toGroupTalkPage(gid, true, null);
        mav.addObject("msg", "Option: " + opt + " was done successfully!");
        return mav;
    }

    /**
     * 负责处理切分任务的请求
     * @param fid 父任务的id
     * @param gid 群组的id
     * @param tasksTO 子任务的信息
     * @return 回到群聊的详细信息页面，且直接打开tasks标签页
     */
    @PutMapping("/split")
    public ModelAndView doSplitTask(Integer fid, Integer gid, TasksTO tasksTO){

        //TODO 权限验证，这里暂时忽略，理由同上

        if(tasksTO.getTasks() == null)
            return groupController.toGroupTalkPage(gid, true, "Split cancelled!");
        taskService.splitTask(fid, gid, tasksTO.getTasks());
        return groupController.toGroupTalkPage(gid, true, "Split successfully!");
    }

    /**
     * 前往"我接收的任务"页面
     * @return 任务列表页面
     */
    @GetMapping("/my")
    public ModelAndView toMyAcceptedTasksPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.TASK_LIST);
        mav.addObject("tasks", taskService.selectTasksByAccUserId(WebHelper.getCurrentUser().getId()));
        return mav;
    }

    /**
     * 处理用户的删除任务的AJAX请求
     * @param tid 任务的id
     * @return 处理结果
     */
    @DeleteMapping
    @ResponseBody
    public String doDeleteTask(Integer tid){
        Task task = taskService.selectTaskById(tid);
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), task.getGroupId(), true);
        if(membership.getRole().equals(Role.NORMAL))
            return "You have no right to delete this task!";
        if(task.getHasSon())
            return "Could not delete a father task!";
        if(task.getAcceptorPubInfo() != null)
            return "This task has been accepted!";
        taskService.deleteFullTaskById(tid);
        return "OK";
    }
}