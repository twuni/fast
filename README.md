# Federated Asynchronous Transport (FAST)

## Overview

Federated Asynchronous Transport (FAST) is a spiritual successor to eXtensible Messaging and Presence Protocol (XMPP/Jabber). The intention of FAST is to embrace minimalism in the core features it offers, while retaining the ability for applications to offer additional features at the layer above. The core principles of FAST are as follows:

 * Unlike XMPP, FAST is designed to be extremely lightweight. Whereas XMPP communicates via XML and requires extensive negotiation in order to establish a stream, FAST communicates in binary using a handful of commands.

 * Much like its predecessor, FAST is designed to be a federated system where packets are routed through one or more nodes before finally reaching their destination. Like email and XMPP when used with an offline message queue, the sender and the recipient of a packet need not be online at the same time in order for communications to occur between them.

 * A FAST node may be in exactly one of three states: Detached, Attached, or Trusted.

   * **Detached**: The node is not connected to any other nodes. When in this state, no packets may be sent or received.

   * **Attached**: The node is attached to at least one other node. When in this state, protocol negotiation has completed, but trust has not yet been established.

   * **Trusted**: The node is attached to at least one other node wherein mutual trust has been established. When in this state, packets may be sent and received.

 * When establishing a session between two nodes, there is an **initiator** and a **responder**. The *initiator* is designated to be the node initiating the session. The other node is the *responder*. The *responder* assigns a session ID to mutually identify the session going forward.

 * After establishing a session, the *initiator* must request an address from the *responder* -- this is done via **authentication**. If the *responder* accepts the credential provided by the *initiator* for authentication, then it assigns an **address** to the *initiator*.

 * An **address** is a long-term identifier for a node within the FAST ecosystem, and is expected to persist across multiple sessions.

 * Once a session has been assigned an address, packets can be routed to/from that session until it is destroyed or disconnected.

## API Documentation

[Javadocs][Javadoc] are available.

 [Javadoc]: https://docs.twuni.org/repository/libs-snapshot/org/twuni/fast/1.0.0-SNAPSHOT/

## License

Copyright 2014 Twuni

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

