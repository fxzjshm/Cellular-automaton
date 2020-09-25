package com.entermoor.cellular_automaton;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.entermoor.cellular_automaton.android.opencl.AndroidOpenCLLoader;
import com.entermoor.cellular_automaton.android.vulkan.AndroidVulkanLoader;
import com.entermoor.cellular_automaton.updater.AndroidOpenCLUpdater;
import com.entermoor.cellular_automaton.updater.AndroidVulkanUpdater;

import java.io.File;
import java.io.IOException;

public class AndroidLauncher extends AndroidApplication {

    public static String LD_LIBRARY_PATH = "/vendor/lib64:/vendor/lib64/hw:/odm/lib64:/system/lib64:" +
            "/system/vendor/lib64:/system/lib64/drm:/system/lib64/extractors:/system/lib64/hw:/product/lib64:" +
            "/system/framework:/system/app:/system/priv-app:/vendor/framework:/vendor/app:/vendor/priv-app:" +
            "/system/vendor/framework:/system/vendor/app:/system/vendor/priv-app:/odm/framework:" +
            "/odm/app:/odm/priv-app:/oem/app:/product/framework:/product/app:/product/priv-app:" +
            "/data:/mnt/expand:/apex/com.android.runtime/lib64/bionic:/system/lib64/bootstrap";
    public static String[] searchPaths = LD_LIBRARY_PATH.split(":"),
            default_so_paths = {
                    "/system/lib/libOpenCL.so",
                    "/system/vendor/lib/libOpenCL.so",
                    "/system/vendor/lib/egl/libGLES_mali.so",
                    "/system/vendor/lib64/egl/libGLES_mali.so",
                    "/system/vendor/lib64/egl/libGLES_1_mali.so", // MI 5C
                    "/system/lib64/egl/libGLES_mali.so",
                    "/system/vendor/lib/libPVROCL.so",
                    "/data/data/org.pocl.libs/files/lib/libpocl.so"
            };
    public static String filesDirPath, destFileName = "libOpenCL_1.so";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final CellularAutomaton main = new CellularAutomaton();
        System.loadLibrary("cellular-automaton-android-jni");

        filesDirPath = getApplicationContext().getFilesDir().getAbsolutePath();
        prepareOpenCLLibrary();

        if (AndroidOpenCLLoader.loadOpenCLLibrary() == 0) {
            final AndroidOpenCLUpdater CLUpdater = new AndroidOpenCLUpdater();
            main.updaters.add(CLUpdater);
            CellularAutomaton.asyncExecutor.submit(new AsyncTask<Object>() {
                @Override
                public Object call() {
                    try {
                        CLUpdater.init();
                        main.updater = CLUpdater;
                    } catch (Exception e) {
                        synchronized (System.err) {
                            System.err.println("[AndroidLauncher] Cannot init device " + CLUpdater.deviceName
                                    + " on platform " + CLUpdater.platformName);
                            e.printStackTrace(System.err);
                            main.updaters.remove(CLUpdater);
                        }
                    }
                    return null;
                }
            });
        }
        if (AndroidVulkanLoader.loadVulkanLibrary() == 0) {
            final AndroidVulkanUpdater VKUpdater = new AndroidVulkanUpdater();
            main.updaters.add(VKUpdater);
            CellularAutomaton.asyncExecutor.submit(new AsyncTask<Object>() {
                @Override
                public Object call() {
                    try {
                        VKUpdater.init();
                    } catch (Exception e) {
                        synchronized (System.err) {
                            System.err.println("[AndroidLauncher] Cannot init device " + VKUpdater.deviceName
                                    + " on platform " + VKUpdater.platformName);
                            e.printStackTrace(System.err);
                            main.updaters.remove(VKUpdater);
                        }
                    }
                    return null;
                }
            });
        }
        initialize(main, config);
    }

    public static void prepareOpenCLLibrary() {
        setenv("LD_LIBRARY_PATH", filesDirPath + ":" + LD_LIBRARY_PATH, true);
        String destFilePath = filesDirPath + "/" + destFileName;
        File destFile = new File(destFilePath);
        if (!destFile.exists()) {
            try {
                for (String default_so_path : default_so_paths) {
                    File soFile = new File(default_so_path);
                    if (!soFile.exists()) continue;
                    try {
                        copyAndLoadLibrary(default_so_path);
                    } catch (UnsatisfiedLinkError | IOException | InterruptedException e) {
                        Log.d("prepareOpenCLLibrary", e.getMessage());
                        // failed
                        File copiedLibrary = new File(filesDirPath + "/" + soFile.getName());
                        if (copiedLibrary.exists()) copiedLibrary.delete();
                        continue;
                    }
                    String copiedLibraryPath = filesDirPath + "/" + soFile.getName();
                    load(copiedLibraryPath);
                    // success
                    Log.i("prepareOpenCLLibrary", "load " + copiedLibraryPath + "succeeded");
                    File copiedLibraryFile = new File(copiedLibraryPath);
                    Runtime.getRuntime().exec(new String[]{"mv", copiedLibraryPath, destFilePath}).waitFor();
                    Log.i("prepareOpenCLLibrary", "moved " + copiedLibraryPath + "to" + destFilePath);
                    load(destFilePath);
                    break;
                }
            } catch (IOException | RuntimeException | UnsatisfiedLinkError | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                copyAndLoadLibrary(destFilePath);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
        setenv("LIBOPENCL_SO_PATH", destFile.getAbsolutePath(), true);
    }

    public static void copyAndLoadLibrary(String so_path) throws IOException, InterruptedException {
        Log.d("copyAndLoadLibrary", so_path);
        File so_file = new File(so_path);
        String so_name = so_file.getName(), target = filesDirPath + "/" + so_name;
        File targetFile = new File(target);
        if (!(targetFile.exists() && targetFile.length() == so_file.length())) {
            Runtime.getRuntime().exec(new String[]{"cp", so_path, filesDirPath + "/"}).waitFor();
            Log.i("copyAndLoadLibrary", "copied " + so_path + " to " + target);
        }
        String libFileName;
        while (true) {
            try {
                load(target);
                Log.d("copyAndLoadLibrary", "load " + target + " succeeded");

                break;
            } catch (UnsatisfiedLinkError e) {
                String message = e.getMessage();
                Log.d("copyAndLoadLibrary", "load " + target + " failed: " + message);

                // dlopen failed: library "*" needed or dlopened by "*" is not accessible for the namespace "*"
                // dlopen failed: library "*" not found
                if (message.startsWith("dlopen failed: library")) {
                    int l = message.indexOf('\"'), r = message.indexOf('\"', l + 1);
                    libFileName = message.substring(l + 1, r);
                    if (libFileName.equals(so_name)) {
                        throw e;
                    }
                } else {
                    // dlopen failed: "/data/data/com.entermoor.cellular_automaton/files/libOpenCL.so" is 32-bit instead of 64-bit
                    throw e;
                }
            }
            copyAndLoadLibrary(lookupLibrary(libFileName));
        }
        Log.d("copyAndLoadLibrary", "will load " + target);
        load(target);
    }

    public static long load(String target) {
        long ret = __dlopen(target);
        if (ret == 0) throw new UnsatisfiedLinkError(dlerr());
        Log.d("load", "loaded " + target);
        return ret;
    }

    public static String lookupLibrary(String libFileName) {
        Log.d("lookupLibrary", libFileName);
        for (String dirPath : searchPaths) {
            String soPath = dirPath + "/" + libFileName;
            File soFile = new File(soPath);
            if (!soFile.exists()) continue;
            return soPath;
        }
        throw new RuntimeException("Cannot find " + libFileName);
    }

    public static native void setenv(String name, String value, boolean override);

    public static native long __dlopen(String filename);

    public static native String dlerr();
}
