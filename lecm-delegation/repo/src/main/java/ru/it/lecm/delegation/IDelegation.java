package ru.it.lecm.delegation;

import org.json.JSONObject;

/**
 * Интерфейс для поддержки делегирований.
 * @author rabdullin
 */
public interface IDelegation {

	JSONObject test(JSONObject args);

	IDelegationDescriptor getDelegationDescriptor ();
}
