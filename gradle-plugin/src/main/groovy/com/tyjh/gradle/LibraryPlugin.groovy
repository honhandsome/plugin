package com.tyjh.gradle

class LibraryPlugin extends ProjectPlugin {

    @Override
    boolean isApplication() {
        return false
    }
}