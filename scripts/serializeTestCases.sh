#!/bin/bash

i=0 
tot=`cat listTupleFull.txt | wc -l`
tmpDir=`mktemp -p "/tmp" -d "SerializationDir-XXX"`

echo "The serialized filed will be stored in: ${tmpDir}"
echo "Start Processing ..."

for item in `cat listTupleFull.txt`
do 
    (( i=i+1 ))
    policyID=`echo $item | cut -d "|" -f 1`
    requestID=`echo $item | cut -d "|" -f 2`
    echo "($i / $tot) $policyID $requestID"
    policy="../src/test/XACMLResources/PoliciesRequestsResponses/xacml_Mutated_Policies/SomeRequest/${policyID}.xml"
    request="../src/test/XACMLResources/PoliciesRequestsResponses/Requests/SomeRequest/${policyID}/Multiple_Comb/${requestID}.xml"
    response="../src/test/XACMLResources/PoliciesRequestsResponses/Responses/SomeResponses/${policyID}/response_${requestID}.xml"
    out="${tmpDir}/${policyID}_req_${requestID}.xml"
    cat $policy > $out
    cat $request >> $out
    cat $response >> $out
done

echo "... done"
