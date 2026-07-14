# clean-java.io

Zero-dependency Java I/O primitives — buffered/reusable data input and output streams (`DataInputBufferedReusable`, `DataOutputBufferedReusable`, `DataInputByteArrayFast`), stream adapters (`ReaderInputStream`, `WriterOutputStream`, `WrapInputStream`), and UTF-8 helpers, built to avoid allocation in hot paths rather than wrap `java.io` types with convenience.

Pure JDK, no external dependencies — that's the point of the "clean" prefix, shared with its sibling packages (`clean-java.util`, `clean-jdbc.util`, ...). It's meant to be includable anywhere without pulling in a dependency chain; `ae3.api` depends on it directly for exactly this reason.
