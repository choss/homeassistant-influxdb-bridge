package org.insanedevelopment.hass.influx.gatherer.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class FilterPathSpec {

	private String entityPathSpec;
	private List<String> captureAttributes = new ArrayList<>();
	private boolean captureState = true;
	private boolean hasWildcard;

	public FilterPathSpec(String entityPathSpec) {
		Validate.notEmpty(entityPathSpec, "Entity path spec is not allowed to be empty");
		Validate.isTrue(StringUtils.countMatches(entityPathSpec, '*') <= 1, "Entity path spec can only have 1 wildcard");
		if (StringUtils.contains(entityPathSpec, '*')) {
			Validate.isTrue(StringUtils.endsWith(entityPathSpec, "*"), "Entity path spec wildcard needs to be at the end");
		}
		this.entityPathSpec = StringUtils.substringBefore(entityPathSpec, "*").trim();
		hasWildcard = StringUtils.endsWith(entityPathSpec, "*");
	}

	public boolean matches(String entityId) {
		if (hasWildcard) {
			return StringUtils.startsWithIgnoreCase(entityId.trim(), entityPathSpec);
		} else {
			return StringUtils.equals(entityId.trim(), entityPathSpec);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterPathSpec [entityPathSpec=");
		builder.append(entityPathSpec);
		builder.append(", hasWildcard=");
		builder.append(hasWildcard);
		builder.append(", captureAttributes=");
		builder.append(captureAttributes);
		builder.append(", captureState=");
		builder.append(captureState);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityPathSpec == null) ? 0 : entityPathSpec.hashCode());
		result = prime * result + (hasWildcard ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FilterPathSpec other = (FilterPathSpec) obj;
		if (entityPathSpec == null) {
			if (other.entityPathSpec != null) {
				return false;
			}
		} else if (!entityPathSpec.equals(other.entityPathSpec)) {
			return false;
		}
		if (hasWildcard != other.hasWildcard) {
			return false;
		}
		return true;
	}

	public String getEntityPathSpec() {
		return entityPathSpec;
	}

	public List<String> getCaptureAttributes() {
		return captureAttributes;
	}

	public boolean isCaptureState() {
		return captureState;
	}

	public boolean isHasWildcard() {
		return hasWildcard;
	}

}
