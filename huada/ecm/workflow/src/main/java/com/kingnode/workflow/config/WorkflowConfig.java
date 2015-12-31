package com.kingnode.workflow.config;
/**
 * @author chirs@zhoujin.com (Chirs Chou)
 */
public class WorkflowConfig implements java.io.Serializable{
    //审批同意常亮，同意或同意并指定下一审批人
    public static final String AGREE_AND_OVER="agree_and_over";//同意并结束
    public static final String AGREE_AND_NEXT="agree_and_next";//同意并指定下一审批人
}
