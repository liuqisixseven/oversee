package com.chd.common.constant;

public class OverseeConstants {

    /**
     * 数据类型
     */
    public interface DataType{
        // -1 无效 1 有效
        /** 1-启用 */
        public static final int Enable = 1;
        /** 0-禁用 */
        public static final int Disabled = -1;
    }

    /**
     * 提交状态
     */
    public interface  SubmitState{
        public static final int Draft=0;
        public static final int Submit=1;
        public static final int Complete=2;

        public static final int DAILY_MANAGEMENT=3;

//        不再纳入整改
        public static final int NoRectification=-1;

        public static final int UnderReview = 5;
    }

    public interface  SpecificType{
        /**
         * 问题描述
         */
        public static final int ISSUE=0;
        /**
         * 整改措施
         */
        public static final int IMPROVE_ACTION=1;
    }
}
