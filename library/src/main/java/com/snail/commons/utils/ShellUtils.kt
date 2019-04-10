package com.snail.commons.utils

import java.io.*

/**
 * Created by zeng on 2016/12/28.
 * 执行shell命令工具类
 */

object ShellUtils {
    const val COMMAND_SU = "su"
    const val COMMAND_SH = "sh"
    const val COMMAND_EXIT = "exit\n"
    const val COMMAND_LINE_END = "\n"

    /**
     * check whether has root permission
     */
    @JvmStatic 
    fun hasRootPermission(): Boolean {
        return execCommand("echo root", true).result == 0
    }


    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot  whether need to run with root
     * @see ShellUtils.execCommand
     */
    @JvmStatic 
    fun execCommand(command: String, isRoot: Boolean): CommandResult {
        return execCommand(arrayOf(command), isRoot)
    }

    /**
     * execute shell commands
     *
     * @param commands        command list
     * @param isRoot          whether need to run with root
     * @see ShellUtils.execCommand
     */
    @JvmStatic 
    fun execCommand(commands: List<String>?, isRoot: Boolean): CommandResult {
        return execCommand(commands?.toTypedArray(), isRoot)
    }


    /**
     * execute shell commands.
     *
     * @param commands        command array
     * @param isRoot          whether need to run with root
     * @return if isNeedResultMsg is false, [CommandResult.successMsg] is null and
     * [CommandResult.errorMsg] is null.
     * <br></br>
     * if [CommandResult.result] is -1, there maybe some excepiton.
     */
    @JvmStatic 
    fun execCommand(commands: Array<String>?, isRoot: Boolean): CommandResult {
        var result = -1
        if (commands == null || commands.isEmpty()) {
            return CommandResult(result, "", "")
        }

        var process: Process? = null
        var os: DataOutputStream? = null
        var successMsgRunnable: ReadMsgRunnable? = null
        var errorMsgRunnable: ReadMsgRunnable? = null
        try {
            process = Runtime.getRuntime().exec(if (isRoot) COMMAND_SU else COMMAND_SH)
            os = DataOutputStream(process!!.outputStream)
            for (command in commands) {
                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.toByteArray())
                os.writeBytes(COMMAND_LINE_END)
                os.flush()
            }
            os.writeBytes(COMMAND_EXIT)
            os.flush()
            successMsgRunnable = ReadMsgRunnable(process.inputStream)
            errorMsgRunnable = ReadMsgRunnable(process.errorStream)
            Thread(successMsgRunnable).start()
            Thread(errorMsgRunnable).start()
            result = process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            process?.destroy()
        }
        return CommandResult(result, successMsgRunnable?.msg ?: "", errorMsgRunnable?.msg ?: "")
    }

    private class ReadMsgRunnable internal constructor(private val inputStream: InputStream) : Runnable {
        private val sb: StringBuilder = StringBuilder()

        internal val msg: String
            get() = sb.toString()

        override fun run() {
            var result: BufferedReader? = null
            try {
                result = BufferedReader(InputStreamReader(inputStream))
                var s = result.readLine()
                while (s != null) {
                    sb.append(s)
                    s = result.readLine()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    result?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * result of command.
     *
     *
     * [CommandResult.result] means result of command, 0 means normal, else means error, same to excute in
     * linux shell
     * <br></br>
     * [CommandResult.successMsg] means success message of command result
     * <br></br>
     * [CommandResult.errorMsg] means error message of command result
     *
     * @author Trinea 2013-5-16
     */
    class CommandResult {

        /**
         * result of command
         */
        var result: Int = 0
        /**
         * success message of command result
         */
        var successMsg = ""
        /**
         * error message of command result
         */
        var errorMsg = ""


        constructor(result: Int) {
            this.result = result
        }


        constructor(result: Int, successMsg: String, errorMsg: String) {
            this.result = result
            this.successMsg = successMsg
            this.errorMsg = errorMsg
        }
    }
}