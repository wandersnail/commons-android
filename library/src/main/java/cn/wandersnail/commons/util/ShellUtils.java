package cn.wandersnail.commons.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 执行shell命令工具类
 * <p>
 * date: 2019/8/7 17:02
 * author: zengfansheng
 */
public class ShellUtils {
    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    /**
     * check whether has root permission
     */
    public static boolean hasRootPermission() {
        return execCommand("echo root", true).result == 0;
    }


    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot  whether need to run with root
     * @see ShellUtils#execCommand
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot);
    }

    /**
     * execute shell commands
     *
     * @param commands command list
     * @param isRoot   whether need to run with root
     * @see ShellUtils#execCommand
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands.toArray(new String[0]), isRoot);
    }


    /**
     * execute shell commands.
     *
     * @param commands command array
     * @param isRoot   whether need to run with root
     * @return if isNeedResultMsg is false, [CommandResult.successMsg] is null and
     * [CommandResult.errorMsg] is null.
     * <br></br>
     * if [CommandResult.result] is -1, there maybe some excepiton.
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, "", "");
        }

        Process process = null;
        DataOutputStream os = null;
        ReadMsgRunnable successMsgRunnable = null;
        ReadMsgRunnable errorMsgRunnable = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            successMsgRunnable = new ReadMsgRunnable(process.getInputStream());
            errorMsgRunnable = new ReadMsgRunnable(process.getErrorStream());
            new Thread(successMsgRunnable).start();
            new Thread(errorMsgRunnable).start();
            result = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsgRunnable == null ? "" : successMsgRunnable.getMsg(),
                errorMsgRunnable == null ? "" : errorMsgRunnable.getMsg());
    }

    private static class ReadMsgRunnable implements Runnable {
        private InputStream inputStream;
        private StringBuilder sb = new StringBuilder();

        ReadMsgRunnable(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        String getMsg() {
            return sb.toString();
        }

        @Override
        public void run() {
            BufferedReader result = null;
            try {
                result = new BufferedReader(new InputStreamReader(inputStream));
                String s = result.readLine();
                while (s != null) {
                    sb.append(s);
                    s = result.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(result);
            }
        }
    }

    /**
     * result of command.
     * <p>
     * <p>
     * [CommandResult.result] means result of command, 0 means normal, else means error, same to excute in
     * linux shell
     * <br></br>
     * [CommandResult.successMsg] means success message of command result
     * <br></br>
     * [CommandResult.errorMsg] means error message of command result
     *
     * @author Trinea 2013-5-16
     */
    public static class CommandResult {

        /**
         * result of command
         */
        private int result;
        /**
         * success message of command result
         */
        private String successMsg = "";
        /**
         * error message of command result
         */
        private String errorMsg = "";


        public CommandResult(int result) {
            this.result = result;
        }


        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        public int getResult() {
            return result;
        }

        public String getSuccessMsg() {
            return successMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }
}
