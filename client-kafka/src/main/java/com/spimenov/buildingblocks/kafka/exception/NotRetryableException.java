/*
 * Copyright (C) 2020 adidas AG.
 */

package com.spimenov.buildingblocks.kafka.exception;

/**
 * Extend this class by exceptions that don't lead to retry.
 *
 * @author Stanislav Pimenov
 */
public abstract class NotRetryableException extends RuntimeException {

}
