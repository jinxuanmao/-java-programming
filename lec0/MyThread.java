public class ThreadExample {

    // 1. 继承 Thread 类的方式 (继承 Thread 类来创建线程)
    // 在这个例子中，MyThread 类不是 public，因此只能在 ThreadExample.java 文件中定义
    static class MyThread extends Thread {
        private String name;

        // 构造方法，传入线程名称
        public MyThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                // 打印当前线程的名称和运行次数
                System.out.println(name + " 线程运行中: " + i);
                try {
                    // 让线程休眠 0.1 秒，模拟线程的延迟，展示多线程执行顺序的变化
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    // 如果当前线程被中断，重新设置中断标志
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 2. 实现 Runnable 接口的方式 (更常见的推荐方式)
    static class MyRunnable implements Runnable {
        private String name;

        public MyRunnable(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                // 打印当前线程的名称和运行次数
                System.out.println(name + " Runnable 正在运行: " + i);
                try {
                    // 让线程休眠 0.1 秒
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // 如果当前线程被中断，重新设置中断标志
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        // 1. 使用 Thread 类继承方式创建并启动线程
        System.out.println("--- 1. 使用 Thread 类继承方式启动线程 ---");
        
        // 创建 MyThread 实例并调用 start() 方法
        MyThread thread1 = new MyThread("Thread-A");
        thread1.start(); // 启动新线程并调用 run() 方法

        System.out.println("--- 2. 使用 Runnable 接口实现方式启动线程 ---");

        // 创建 MyRunnable 实例
        MyRunnable runnable2 = new MyRunnable("Runnable-B");
        // 创建 Thread 对象并传入 MyRunnable 实例
        Thread thread2 = new Thread(runnable2); 
        thread2.start(); // 启动新线程并调用 run() 方法

        // 主线程继续执行
        System.out.println("主线程结束.");
    }
}
