/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package io.atomix.copycat.server.protocol.net.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.atomix.copycat.server.protocol.request.PollRequest;

/**
 * TCP poll request.
 *
 * @author <a href="http://github.com/kuujo>Jordan Halterman</a>
 */
public class NetPollRequest extends PollRequest implements RaftNetRequest {
  private final long id;

  public NetPollRequest(long id, long term, int candidate, long logIndex, long logTerm) {
    super(term, candidate, logIndex, logTerm);
    this.id = id;
  }

  @Override
  public long id() {
    return id;
  }

  @Override
  public Type type() {
    return Types.POLL_REQUEST;
  }

  /**
   * TCP poll request builder.
   */
  public static class Builder extends PollRequest.Builder {
    private final long id;

    public Builder(long id) {
      this.id = id;
    }

    @Override
    public PollRequest copy(PollRequest request) {
      return new NetPollRequest(id, request.term(), request.candidate(), request.logIndex(), request.logTerm());
    }

    @Override
    public PollRequest build() {
      return new NetPollRequest(id, term, candidate, logIndex, logTerm);
    }
  }

  /**
   * Poll request serializer.
   */
  public static class Serializer extends RaftNetRequest.Serializer<NetPollRequest> {
    @Override
    public void write(Kryo kryo, Output output, NetPollRequest request) {
      output.writeLong(request.id);
      output.writeLong(request.term);
      output.writeInt(request.candidate);
      output.writeLong(request.logIndex);
      output.writeLong(request.logTerm);
    }

    @Override
    public NetPollRequest read(Kryo kryo, Input input, Class<NetPollRequest> type) {
      return new NetPollRequest(input.readLong(), input.readLong(), input.readInt(), input.readLong(), input.readLong());
    }
  }
}
