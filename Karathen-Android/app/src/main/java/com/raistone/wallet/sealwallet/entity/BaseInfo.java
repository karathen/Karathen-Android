package com.raistone.wallet.sealwallet.entity;

public class BaseInfo {


    /**
     * error : {"code":500,"data":null,"executable":"/root/AppServer/3.5venv/bin/python","message":"OtherError: get_token_info() takes 2 positional arguments but 9 were given","name":"OtherError","stack":"Traceback (most recent call last):\n  File \"/root/AppServer/3.5venv/lib/python3.5/site-packages/flask_jsonrpc/site.py\", line 233, in response_obj\n    R = apply_version[version](method, D['params'])\n  File \"/root/AppServer/3.5venv/lib/python3.5/site-packages/flask_jsonrpc/site.py\", line 176, in apply_version_2_0\n    return f(**encode_kw(p)) if type(p) is dict else f(*p)\nTypeError: get_token_info() takes 2 positional arguments but 9 were given\n"}
     * id : 1
     * jsonrpc : 2.0
     */

    private ErrorBean error;
    private String id;
    private String jsonrpc;

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public static class ErrorBean {
        /**
         * code : 500
         * data : null
         * executable : /root/AppServer/3.5venv/bin/python
         * message : OtherError: get_token_info() takes 2 positional arguments but 9 were given
         * name : OtherError
         * stack : Traceback (most recent call last):
         File "/root/AppServer/3.5venv/lib/python3.5/site-packages/flask_jsonrpc/site.py", line 233, in response_obj
         R = apply_version[version](method, D['params'])
         File "/root/AppServer/3.5venv/lib/python3.5/site-packages/flask_jsonrpc/site.py", line 176, in apply_version_2_0
         return f(**encode_kw(p)) if type(p) is dict else f(*p)
         TypeError: get_token_info() takes 2 positional arguments but 9 were given

         */

        private int code;
        private Object data;
        private String executable;
        private String message;
        private String name;
        private String stack;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getExecutable() {
            return executable;
        }

        public void setExecutable(String executable) {
            this.executable = executable;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStack() {
            return stack;
        }

        public void setStack(String stack) {
            this.stack = stack;
        }
    }
}
