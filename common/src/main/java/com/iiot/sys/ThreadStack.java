package com.iiot.sys;

public class ThreadStack {
	static public String getCurrentStack(){
		StringBuilder sb = new StringBuilder();
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		int index = 0;
		for(StackTraceElement stack : stacks){
			//跳过自己两层:
			//java.lang.Thread.getStackTrace(Thread.java:1552)
			//com.iiot.sys.ThreadStack.getCurrentStack(ThreadStack.java:6)
			if(index++ < 2){
				continue;
			}
			String line = " at "+stack.getClassName()+"."+stack.getMethodName()
			+"("+stack.getFileName()+":"+stack.getLineNumber()+")\r\n";
			sb.append(line);
			
		}
		return sb.toString();
	}
}
