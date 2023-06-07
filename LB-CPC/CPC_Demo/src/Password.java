
public class Password {

	static char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

	public static void main(String[] args) {

		for (char c : alphabet) {
			double t = System.currentTimeMillis();
			String poss = "jami"+c;
			System.out.println(checkPW(poss));
			double elapsed = System.currentTimeMillis()-t;
			System.out.println(poss + ":"+elapsed);
		}

	}

























	public static boolean checkPW(String pw) {
		char[] password = {'j','a','m','i','e'};
		char[] candidate = pw.toCharArray();

		for (int x = 0; x < password.length; x ++) {

			if (x>=candidate.length)
				return false;
			if (password[x]  != candidate[x])
				return false;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
