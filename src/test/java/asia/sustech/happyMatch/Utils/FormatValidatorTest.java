package asia.sustech.happyMatch.Utils;

class FormatValidatorTest {
    public static void main(String[] args) {
//        System.out.println(FormatValidator.isEmailValid("?"));
//        System.out.println(FormatValidator.isEmailValid("123@ba.com"));
//        System.out.println(FormatValidator.isEmailValid("@ba.com"));
//        System.out.println(FormatValidator.isEmailValid("aaa@.com"));
//        System.out.println(FormatValidator.isEmailValid("aaa@ba."));
//        System.out.println(FormatValidator.isEmailValid("bba@asdb.iouou9"));
//        System.out.println(FormatValidator.isEmailValid("bba@sdf.i.io."));
//        System.out.println(FormatValidator.isEmailValid("?"));
        System.out.println(FormatValidator.isMapFormatInvalid("""
                2 8 8 2 2 9
                1 0 10 5
                2 0 10 6
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0
                0 0 0 0 0 0 0 0"""));
    }
}