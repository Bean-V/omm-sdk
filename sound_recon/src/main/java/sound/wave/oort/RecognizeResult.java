package sound.wave.oort;

import java.io.Serializable;

public class RecognizeResult implements Serializable {

    /**
     * code : 200
     * data : {"task_id":"1abbb8d5-a2c1-4c34-93e1-59f469aef7c0","workflow_run_id":"066d1452-213e-4c74-9efc-c57d1c4e7257","data":{"id":"066d1452-213e-4c74-9efc-c57d1c4e7257","workflow_id":"a17598d7-b5cd-4ab1-b209-f49a6699b532","status":"succeeded","outputs":{"text":"你好你好。"},"elapsed_time":0.579714,"total_tokens":0,"total_steps":3,"created_at":1751100211,"finished_at":1751100212}}
     * msg : 成功
     */

    private int code;
    private DataBeanX data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private String msg;


    public static class DataBeanX implements Serializable {
        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getWorkflow_run_id() {
            return workflow_run_id;
        }

        public void setWorkflow_run_id(String workflow_run_id) {
            this.workflow_run_id = workflow_run_id;
        }

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        /**
         * task_id : 1abbb8d5-a2c1-4c34-93e1-59f469aef7c0
         * workflow_run_id : 066d1452-213e-4c74-9efc-c57d1c4e7257
         * data : {"id":"066d1452-213e-4c74-9efc-c57d1c4e7257","workflow_id":"a17598d7-b5cd-4ab1-b209-f49a6699b532","status":"succeeded","outputs":{"text":"你好你好。"},"elapsed_time":0.579714,"total_tokens":0,"total_steps":3,"created_at":1751100211,"finished_at":1751100212}
         */

        private String task_id;
        private String workflow_run_id;
        private DataBean data;

        public static class DataBean implements Serializable {
            /**
             * id : 066d1452-213e-4c74-9efc-c57d1c4e7257
             * workflow_id : a17598d7-b5cd-4ab1-b209-f49a6699b532
             * status : succeeded
             * outputs : {"text":"你好你好。"}
             * elapsed_time : 0.579714
             * total_tokens : 0
             * total_steps : 3
             * created_at : 1751100211
             * finished_at : 1751100212
             */

            private String id;
            private String workflow_id;
            private String status;
            private OutputsBean outputs;
            private double elapsed_time;

            public int getTotal_tokens() {
                return total_tokens;
            }

            public void setTotal_tokens(int total_tokens) {
                this.total_tokens = total_tokens;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getWorkflow_id() {
                return workflow_id;
            }

            public void setWorkflow_id(String workflow_id) {
                this.workflow_id = workflow_id;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public OutputsBean getOutputs() {
                return outputs;
            }

            public void setOutputs(OutputsBean outputs) {
                this.outputs = outputs;
            }

            public double getElapsed_time() {
                return elapsed_time;
            }

            public void setElapsed_time(double elapsed_time) {
                this.elapsed_time = elapsed_time;
            }

            public int getTotal_steps() {
                return total_steps;
            }

            public void setTotal_steps(int total_steps) {
                this.total_steps = total_steps;
            }

            public int getCreated_at() {
                return created_at;
            }

            public void setCreated_at(int created_at) {
                this.created_at = created_at;
            }

            public int getFinished_at() {
                return finished_at;
            }

            public void setFinished_at(int finished_at) {
                this.finished_at = finished_at;
            }

            private int total_tokens;
            private int total_steps;
            private int created_at;
            private int finished_at;


            public static class OutputsBean implements Serializable {
                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                /**
                 * text : 你好你好。
                 */

                private String text;
            }
        }
    }
}
