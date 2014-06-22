package org.evilsoft.pathfinder.spellbook;

public class SpellListItem extends SectionListItem {
	private int id;
	private String source;
	private String type;
	private String subType;
	private String name;
	private String contentUrl;
	private String description;
	private String school;
	private String subschool;
	private String descriptor;
	private String components;
	private String classes;
	private String className;
	private int level;
	private String magicType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setItem(name);
		setSection(name.substring(0, 1));
	}

	public String toString() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSubschool() {
		return subschool;
	}

	public void setSubschool(String subschool) {
		this.subschool = subschool;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public String buildSchoolLine() {
		StringBuffer sb = new StringBuffer();
		sb.append(school);
		if (subschool != null) {
			sb.append(" (");
			sb.append(subschool);
			sb.append(")");
		}
		if (descriptor != null) {
			sb.append(" [");
			sb.append(descriptor);
			sb.append("]");
		}
		return sb.toString();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
		setSection(level.toString());
	}

	public String getMagicType() {
		return magicType;
	}

	public void setMagicType(String magicType) {
		this.magicType = magicType;
	}
}
