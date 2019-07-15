package com.puresoltechnologies.javafx.reactive;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a broker implementation for a Publish-Subscribe-Pattern.
 *
 * @author Rick-Rainer Ludwig
 */
public class MessageBroker {

    private static MessageBroker instance = null;

    /**
     * This method initializes the broker.
     *
     * @throws IllegalStateException is thrown in case the broker was already
     *                               initialized.
     */
    public static synchronized void initialize() {
	if (instance != null) {
	    throw new IllegalStateException("Broker was initialized already.");
	}
	instance = new MessageBroker();
    }

    /**
     * This method shuts the broker down.
     *
     * @throws IllegalStateException is thrown in case the broker was already
     *                               shutdown or not initialized.
     */
    public static synchronized void shutdown() {
	instance.close();
	instance = null;
    }

    /**
     * This method is used to check the initialization state of this broker.
     *
     * @return <code>true</code> is returned in case the Broker is initialized.
     *         <code>false</code> is returned otherwise.
     */
    public static synchronized boolean isInitialized() {
	return instance != null;
    }

    public static synchronized MessageBroker getStore() {
	return instance;
    }

    private final Map<Topic<?>, SubmissionPublisher<?>> subjects = new HashMap<>();
    private final Map<Topic<?>, List<?>> lastItems = new HashMap<>();

    private final ExecutorService executorService;

    private MessageBroker() {
	executorService = Executors.newCachedThreadPool(new ThreadFactory() {
	    private final AtomicInteger id = new AtomicInteger(0);

	    @Override
	    public Thread newThread(Runnable target) {
		return new Thread(target, "FluxStore-thread-" + id.incrementAndGet());
	    }
	});
    }

    private void close() {
	subjects.values().forEach(subject -> {
	    subject.close();
	});
	executorService.shutdownNow();
	subjects.clear();
    }

    public <T> void publish(Topic<T> topic, T message) {
	SubmissionPublisher<T> subject = assurePresenceOfTopic(topic);
	subject.submit(message);
	List<T> deque = getDeque(topic);
	deque.add(message);
	if (deque.size() > topic.getBufferSize()) {
	    deque.remove(0);
	}
    }

    private <T> List<T> getDeque(Topic<T> topic) {
	@SuppressWarnings("unchecked")
	List<T> deque = (List<T>) lastItems.get(topic);
	return deque;
    }

    public <T> void subscribe(Topic<T> topic, Subscriber<T> subscriber) {
	SubmissionPublisher<T> subject = assurePresenceOfTopic(topic);
	subject.subscribe(subscriber);
	List<T> deque = getDeque(topic);
	deque.stream().forEach(message -> subscriber.onNext(message));
    }

    @SuppressWarnings("unchecked")
    private <T> SubmissionPublisher<T> assurePresenceOfTopic(Topic<T> topic) {
	SubmissionPublisher<T> subject = (SubmissionPublisher<T>) subjects.get(topic);
	if (subject == null) {
	    synchronized (subjects) {
		subject = (SubmissionPublisher<T>) subjects.get(topic);
		if (subject == null) {
		    subject = new SubmissionPublisher<>(executorService, topic.getBufferSize());
		    subjects.put(topic, subject);
		    lastItems.put(topic, new LinkedList<>());
		}
	    }
	}
	return subject;
    }

}
