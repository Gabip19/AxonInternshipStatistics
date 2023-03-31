package axon.statistics.processor.validator;

public interface Validator<E> { // TODO: 03/31/23 maybe change to ArrayValidator
    void validate(E[] data);
}
