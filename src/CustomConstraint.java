import java.util.regex.Pattern;

public class CustomConstraint {

	private String source;
	private String regex;
	private ConstraintPriority priority;
	private boolean mustAppear;
	private Pattern pattern;
	
	public CustomConstraint(String source) {
		this.source = source;
		this.priority = ConstraintPriority.low;//default
	}

	public void setRegex(String regex) {
		this.regex = regex;
		
	}
	
	public String getSource() {
		return source;
	}

	public String getRegex() {
		return regex;
	}

	public ConstraintPriority getPriority() {
		return priority;
	}
	
	public void setPriority(ConstraintPriority cp) {
		this.priority = cp;
	}

	public boolean mustAppear() {
		return mustAppear;
	}
	
	public void mustAppear(boolean ma) {
		this.mustAppear = ma;
	}
	
	public Pattern getPattern() {
		if (pattern == null)
			pattern = Pattern.compile(this.getRegex(), Pattern.CASE_INSENSITIVE);
		return pattern;
	}

}
