package com.javalove.dcp.example;

import java.util.concurrent.TimeUnit;

import com.couchbase.client.core.event.CouchbaseEvent;
import com.couchbase.client.dcp.Client;
import com.couchbase.client.dcp.Client.Builder;
import com.couchbase.client.dcp.ControlEventHandler;
import com.couchbase.client.dcp.DataEventHandler;
import com.couchbase.client.dcp.StreamFrom;
import com.couchbase.client.dcp.StreamTo;
import com.couchbase.client.dcp.SystemEventHandler;
import com.couchbase.client.dcp.config.DcpControl;
import com.couchbase.client.dcp.events.StreamEndEvent;
import com.couchbase.client.dcp.message.DcpDeletionMessage;
import com.couchbase.client.dcp.message.DcpMutationMessage;
import com.couchbase.client.dcp.transport.netty.ChannelFlowController;
import com.couchbase.client.deps.io.netty.buffer.ByteBuf;

public class ClientStarter { 

	public static void main(String[] args) {
		try {
			final String hostname = System.getenv("CB_HOSTNAME");
			final String bucket = System.getenv("BUCKET");
			final String username = System.getenv("CB_USER");
			final String password = System.getenv("CB_PASSWORD");
			final String isNoopTrue = System.getenv("IS_NOOP");
			final String minutesInWaiting = System.getenv("MINUTES_WAITING");

			final boolean isNoopEnable = isNoopTrue != null && isNoopTrue.equalsIgnoreCase("true");
			final long msSleep = TimeUnit.MINUTES.toMillis(Long.parseLong(minutesInWaiting));

			StringBuilder initMsgBuild = new StringBuilder("Starting the client");
			if (isNoopEnable)
				initMsgBuild.append(" with NOOP enabled");
			else
				initMsgBuild.append(" with NOOP desabled, ");
			initMsgBuild.append("with a sleep time of: " + msSleep + " ms");
			System.out.println(initMsgBuild.toString());

			final Builder builder = Client.configure()
					.hostnames(hostname)
					.bucket(bucket)
					.controlParam(DcpControl.Names.CONNECTION_BUFFER_SIZE, 20480)
					.bufferAckWatermark(60)
					.connectTimeout(1L);
			if (username != null)
				builder.username(username);
			if (password != null)
				builder.password(password);
			if (isNoopEnable) {
				builder
				.controlParam(DcpControl.Names.ENABLE_NOOP, "true")
				.controlParam(DcpControl.Names.SET_NOOP_INTERVAL, 60);
			}

			final Client client = builder.build();


			// Don't do anything with control events in this example
			client.controlEventHandler(new ControlEventHandler() {
				@Override
				public void onEvent(ChannelFlowController flowController, ByteBuf event) {
					event.release();
				}
			}); 

			// Print out Mutations and Deletions
			client.dataEventHandler(new DataEventHandler() {
				@Override
				public void onEvent(ChannelFlowController flowController, ByteBuf event) {
					if (DcpMutationMessage.is(event)) {
						System.out.println("Mutation: " + DcpMutationMessage.toString(event));
					} else if (DcpDeletionMessage.is(event)) {
						System.out.println("Deletion: " + DcpDeletionMessage.toString(event));
					}
					event.release();
				}
			});

			// Connect the sockets
			client.connect().await();

			// Initialize the state (start now, never stop)
			client.initializeState(StreamFrom.NOW, StreamTo.INFINITY).await();

			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					// Once the time is over, shutdown.
					client.disconnect().await();
					System.out.println("DCP client shutdown!");
				}
			}));

			// Start streaming on all partitions
			client.startStreaming().await();

			Thread.sleep(msSleep);
		} catch (Exception e) {
			System.err.println("Exception caught: " + e);
		}

	}

}
