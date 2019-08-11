package org.insanedevelopment.hass.influx.gatherer.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class FilterPathSpec {

	// entity rules
	private String entityPathSpec;
	private boolean hasWildcard;

	// attributes which should be as FIELDS in influx
	private List<String> reportAttributes = new ArrayList<>();
	// attributes which should be tags in influx
	private List<String> tagAttributes = new ArrayList<>();
	// should the state be published to influx
	private boolean reportState = true;
	// if present overwrite the name of the state field with the CONTENT of the
	// attribute
	private String overwriteStateFieldWithAttribute;

	protected FilterPathSpec() {
		// for snakeYaml
	}

	public FilterPathSpec(String entityPathSpec) {
		setEntityPathString(entityPathSpec);
	}

	public void setEntityPathString(String entityPathSpec) {
		Validate.notEmpty(entityPathSpec, "Entity path spec is not allowed to be empty");
		Validate.isTrue(StringUtils.countMatches(entityPathSpec, '*') <= 1, "Entity path spec can only have 1 wildcard");
		if (StringUtils.contains(entityPathSpec, '*')) {
			Validate.isTrue(StringUtils.endsWith(entityPathSpec, "*"), "Entity path spec wildcard needs to be at the end");
		}
		this.entityPathSpec = StringUtils.substringBefore(entityPathSpec, "*").trim();
		hasWildcard = StringUtils.endsWith(entityPathSpec, "*");
	}

	public String getEntityPathString() {
		return entityPathSpec + (hasWildcard ? "*" : "");
	}

	public boolean matches(String entityId) {
		if (hasWildcard) {
			return StringUtils.startsWithIgnoreCase(entityId.trim(), entityPathSpec);
		} else {
			return StringUtils.equals(entityId.trim(), entityPathSpec);
		}
	}

	public String getReportAttributesString() {
		return StringUtils.substringBetween(reportAttributes.toString(), "[", "]");
	}

	public String getTagAttributesString() {
		return StringUtils.substringBetween(tagAttributes.toString(), "[", "]");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterPathSpec [entityPathSpec=");
		builder.append(entityPathSpec);
		builder.append(", hasWildcard=");
		builder.append(hasWildcard);
		builder.append(", reportAttributes=");
		builder.append(reportAttributes);
		builder.append(", tagAttributes=");
		builder.append(tagAttributes);
		builder.append(", reportState=");
		builder.append(reportState);
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

	public boolean hasWildcard() {
		return hasWildcard;
	}

	public List<String> getCaptureAttributes() {
		return reportAttributes;
	}

	public List<String> getReportAttributes() {
		return reportAttributes;
	}

	public void setReportAttributes(List<String> reportAttributes) {
		this.reportAttributes = new ArrayList<>(reportAttributes);
	}

	public List<String> getTagAttributes() {
		return tagAttributes;
	}

	public void setTagAttributes(List<String> tagAttributes) {
		this.tagAttributes = new ArrayList<>(tagAttributes);
	}

	public boolean isReportState() {
		return reportState;
	}

	public void setReportState(boolean reportState) {
		this.reportState = reportState;
	}

	public String getOverwriteStateFieldWithAttribute() {
		return overwriteStateFieldWithAttribute;
	}

	public void setOverwriteStateFieldWithAttribute(String overwriteStateFieldWithAttribute) {
		this.overwriteStateFieldWithAttribute = overwriteStateFieldWithAttribute;
	}

}
