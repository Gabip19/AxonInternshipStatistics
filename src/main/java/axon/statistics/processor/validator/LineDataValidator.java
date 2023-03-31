package axon.statistics.processor.validator;

public class LineDataValidator implements Validator<String> {
    @Override
    public void validate(String[] data) {
        if (data.length != 4) { // TODO: 03/31/23 constant
            throw new IllegalArgumentException("Invalid data length.\n");
        }

        validateName(data[0]);
        validateEmail(data[1]);
        validateDeliveryTime(data[2]);
        validateScore(data[3]);
    }

    private void validateName(String fullName) {
        if (!fullName.matches("^([A-ZȘȚÎĂÂ][a-zăâîşșț]+[ -])+[A-ZȘȚÎĂÂ][a-zăâîşșț]+$")) {
            throw new IllegalArgumentException("Invalid name format.");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[a-z][a-z0-9-_.]+@[a-z][a-z0-9-_.]*\\.[a-z]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validateDeliveryTime(String timeFormat) {
        if (!timeFormat.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$")) {
            throw new IllegalArgumentException("Invalid time format.");
        }
    }

    private void validateScore(String score) {
        if (score.matches("^[1-9]0?(\\.[0-9]{2})?$")) {
            float scoreAsFloat = Float.parseFloat(score);
            if (scoreAsFloat < 0 || scoreAsFloat > 10) {
                throw new IllegalArgumentException("Invalid score range.");
            }
        }
        else throw new IllegalArgumentException("Invalid score format.");
    }
}
