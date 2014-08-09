//package com.mbragg.playlister;
//
//import com.mbragg.playlister.configurations.ApplicationConfiguration;
//import com.mbragg.playlister.controllers.ApplicationController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CLApplication implements CommandLineRunner {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ApplicationConfiguration.class, args);
//    }
//
//    @Autowired
//    private ApplicationController applicationController;
//
//    /**
//     * Callback used to run the bean.
//     *
//     * @param args incoming main method arguments
//     * @throws Exception on error
//     */
//    @Override
//    public void run(String... args) throws Exception {
//
//
//        applicationController.buildLibrary("/Users/mbragg/Desktop/samename");
////        applicationController.buildLibrary("/Users/mbragg/Music/iTunes/iTunes Media/Music");
////        applicationController.query("01 The Suburbs.m4a");
//    }
//}
