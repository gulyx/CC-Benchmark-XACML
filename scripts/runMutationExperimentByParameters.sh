#!/bin/bash

LISTID_FILE_CC="TBD"
LISTID_FILE_RANDOM="TBD"
LISTID_FILE_FAST="TBD"

# SET THE DEFAULT CONFIGURATION OF THE "--random" PARAMETER IS SET IN THE case STATEMENT BELOW 
LISTID_FILE_RANDOM="ToBeDefined"

MVN_PROJECT_DIR=".."
SRC_DIR="${MVN_PROJECT_DIR}/src/main/java" 
SRC_TEST_DIR="${MVN_PROJECT_DIR}/src/test/java"
## TARGET_DIR=`mktemp -p /tmp -d CC-Benchmark-XACML-XXX`
TUPLE_SEPARATOR='|'

POLICYID_SYSTEM_PROPERTY_LABEL="policyID" 
REQUESTID_SYSTEM_PROPERTY_LABEL="requestID"
IDSLIST_SYSTEM_PROPERTY_LABEL="idsList"
CT_LABEL="CONFORMANCE_TEST_SUITE"

LISTID_FILE="DefinedBelowByMeansAProcessedOption"
MUTATION_PROFILE="DefinedBelowByMeansAProcessedOption"

MAX_ITERATIONS="1"

BLANK_PLACE_HOLDER="§"

if [[ -z "$1" ]]
then
    echo "USAGE: $0 --balana|--herasaf --variant cc|random|fast [--random <intNumber>]"
    exit 1
else
    case "$1" in
        "--balana" ) 
            MUTATION_PROFILE="balana-Mutation-Profile"

	    LISTID_FILE_FAST="ToBeDefined"
	    LISTID_FILE_CC="ToBeDefined"
	    RANDOMIC_FLAG=""
	    
	    LOCAL_TARGET_DIR_PREFIX="BalanaResults-Mutation"

#	    PATH_PDP_JAR="ToBeDefined"
#            PATH_PDP_SRC="../src/test/resources/lib/sources/org.wso2.balana-1.2.12-sources.jar"
#            TARGET_PACKAGE="org.wso2.balana"
        ;;
        "--herasaf" ) 
            MUTATION_PROFILE="herasaf-Mutation-Profile"

	    LISTID_FILE_FAST="ToBeDefined"
	    LISTID_FILE_CC="ToBeDefined"
	    RANDOMIC_FLAG=""

	    LOCAL_TARGET_DIR_PREFIX="HerasafResults-Mutation"

#            PATH_PDP_JAR="ToBeDefined"
#            PATH_PDP_SRC="../src/test/resources/lib/sources/herasaf-xacml-core-2.0.4-sources.jar"
#            TARGET_PACKAGE="org.herasaf.xacml"
        ;;
    esac
fi

if [[ "$2" != "--variant" || -z "$3" ]]
then
    echo "USAGE: $0 --balana|--herasaf --variant cc|random|fast [--random <intNumber>]"
    exit 1
else
    case "$3" in
        "cc" ) 
            LISTID_FILE="${LISTID_FILE_CC}"
            LOCAL_TARGET_DIR=""
        ;;    
        "random" ) 
            LISTID_FILE="${LISTID_FILE_RANDOM}"
            MAX_ITERATIONS="25"
            LOCAL_TARGET_DIR="${LOCAL_TARGET_DIR_PREFIX}-Random-DoubleCheck"
        ;;
        "fast" ) 
            LISTID_FILE="${LISTID_FILE_FAST}"
            LOCAL_TARGET_DIR=""
        ;;
        * ) 
            echo "UNSUPPORTED VARIANT"
            echo "USAGE: $0 --balana|--herasaf --variant cc|random|fast [--random <intNumber>]"
            exit 1            
        ;;
    esac
fi

if [[ "$4" == "--random" && -n "$5" ]]
then
    RANDOMIC_FLAG="$5"
# else
#    RANDOMIC_FLAG=""
fi

ITERATION=0
while [[ "${ITERATION}" -lt "${MAX_ITERATIONS}" ]]
do
    (( ITERATION=ITERATION+1 ))
    
    if [[ -z "${RANDOMIC_FLAG}" ]]
    then
        TUPLES_LIST=`cat ${LISTID_FILE}`
    else
        TUPLES_LIST=`cat ${LISTID_FILE} | shuf | head -n ${RANDOMIC_FLAG}`
    #    TUPLES_LIST=`cat ${LISTID_FILE} | head -n ${RANDOMIC_FLAG}`
    fi

    TOTAL_TUPLES_TO_BE_PROCESSED=`echo ${TUPLES_LIST} | wc -w`
    TUPLES_PROCESSED="0"
    TEST_LIST=""

    for TUPLE in ${TUPLES_LIST}
    do
        ID_POLICY=`echo ${TUPLE} | cut -d "${TUPLE_SEPARATOR}" -f 1`
        ID_REQUEST=`echo ${TUPLE} | cut -d "${TUPLE_SEPARATOR}" -f 2`
        
        (( TUPLES_PROCESSED=TUPLES_PROCESSED+1 ))
        echo "(${TUPLES_PROCESSED} / ${TOTAL_TUPLES_TO_BE_PROCESSED}) Processing Group: ${ID_POLICY}, ${ID_REQUEST} ..."
            
        TEST_LIST="${ID_POLICY}:${ID_REQUEST},${TEST_LIST}"                        

        echo "... done!"
    done

    cd ${MVN_PROJECT_DIR}
    echo "mvn -P${MUTATION_PROFILE} -D${IDSLIST_SYSTEM_PROPERTY_LABEL}=\"${TEST_LIST}\" clean package org.pitest:pitest-maven:mutationCoverage"
    mvn -P${MUTATION_PROFILE} -D${IDSLIST_SYSTEM_PROPERTY_LABEL}="${TEST_LIST}" clean package org.pitest:pitest-maven:mutationCoverage
    cd -
    
    if [[ -z "${LOCAL_TARGET_DIR}" ]]
    then
        echo "[RESULT-TESTS] The experiments is over, check the results in \"${MVN_PROJECT_DIR}/target/pit-reports\" (if any)"
    else
        LOCAL_TARGET_DIR="${LOCAL_TARGET_DIR}/pit-reports-${ITERATION}"
        mkdir -p ${LOCAL_TARGET_DIR}
        mv "${MVN_PROJECT_DIR}/target/pit-reports" ${LOCAL_TARGET_DIR}
        mv "nohup.out" "${LOCAL_TARGET_DIR}/nohup-${ITERATION}.out"
        echo "[RESULT-TESTS] (${ITERATION}/${MAX_ITERATIONS}) The experiments is over, check the results in \"${LOCAL_TARGET_DIR}\" (if any)"
    fi
done
