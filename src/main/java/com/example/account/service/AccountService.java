package com.example.account.service;

// @Slf4j
// @GrpcService
// public class AccountService extends AccountServiceGrpc.AccountServiceImplBase{
//     @Override
//     public void hello(HelloRequest helloRequest, StreamObserver<HelloResponse> responseObserver) {
//         String message = helloRequest.getValue();

//         final HelloResponse.Builder replyBuilder = HelloResponse.newBuilder().setValue(message);
//         responseObserver.onNext(replyBuilder.build());
//         responseObserver.onCompleted();
//         log.info("SERVER 1 - Returning" + message);
//     }
// }
