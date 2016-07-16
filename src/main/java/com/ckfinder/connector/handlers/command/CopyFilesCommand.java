/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.ckfinder.connector.handlers.command;

import com.ckfinder.connector.configuration.Constants;
import com.ckfinder.connector.configuration.IConfiguration;
import com.ckfinder.connector.data.FilePostParam;
import com.ckfinder.connector.errors.ConnectorException;
import com.ckfinder.connector.utils.AccessControlUtil;
import com.ckfinder.connector.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

/**
 * Class to handle <code>CopyFiles</code> command.
 */
@Slf4j
public class CopyFilesCommand extends XMLCommand implements IPostCommand {

    private List<FilePostParam> files;
    private int filesCopied;
    private int copiedAll;
    private boolean addCopyNode;

    @Override
    protected void createXMLChildNodes(final int errorNum, final Element rootElement)
            throws ConnectorException {
        if (creator.hasErrors()) {
            Element errorsNode = creator.getDocument().createElement("Errors");
            creator.addErrors(errorsNode);
            rootElement.appendChild(errorsNode);
        }

        if (addCopyNode) {
            createCopyFielsNode(rootElement);
        }

    }

    /**
     * creates copy file XML node.
     *
     * @param rootElement XML root node.
     */
    private void createCopyFielsNode(final Element rootElement) {
        Element element = creator.getDocument().createElement("CopyFiles");
        element.setAttribute("copied", String.valueOf(this.filesCopied));
        element.setAttribute("copiedTotal", String.valueOf(this.copiedAll
                + this.filesCopied));
        rootElement.appendChild(element);
    }

    @Override
    protected int getDataForXml() {
        if (!checkIfTypeExists(this.type)) {
            this.type = null;
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_TYPE;
        }

        if (!AccessControlUtil.getInstance().checkFolderACL(
                this.type,
                this.currentFolder,
                this.userRole,
                AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_RENAME
                | AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_DELETE
                | AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_UPLOAD)) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
        }

        try {
            return copyFiles();
        } catch (Exception e) {
            log.error("", e);
        }
        //this code should never be reached
        return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNKNOWN;

    }

    /**
     * copy files from request.
     *
     * @return error code
     */
    private int copyFiles() {
        this.filesCopied = 0;
        this.addCopyNode = false;
        for (FilePostParam file : files) {

            if (!FileUtils.checkFileName(file.getName())) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (Pattern.compile(Constants.INVALID_PATH_REGEX).matcher(
                    file.getFolder()).find()) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }
            if (configuration.getTypes().get(file.getType()) == null) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }
            if (file.getFolder() == null || file.getFolder().isEmpty()) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }
            if (FileUtils.checkFileExtension(file.getName(),
                    this.configuration.getTypes().get(this.type)) == 1) {
                creator.appendErrorNodeChild(
                        Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION,
                        file.getName(), file.getFolder(), file.getType());
                continue;
            }
            // check #4 (extension) - when moving to another resource type,
            //double check extension
            if (!this.type.equals(file.getType())) {
                if (FileUtils.checkFileExtension(file.getName(),
                        this.configuration.getTypes().get(file.getType())) == 1) {
                    creator.appendErrorNodeChild(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_EXTENSION,
                            file.getName(), file.getFolder(), file.getType());
                    continue;

                }
            }
            if (FileUtils.checkIfDirIsHidden(file.getFolder(), this.configuration)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (FileUtils.checkIfFileIsHidden(file.getName(), this.configuration)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_INVALID_REQUEST;
            }

            if (!AccessControlUtil.getInstance().checkFolderACL(file.getType(), file.getFolder(), this.userRole,
                    AccessControlUtil.CKFINDER_CONNECTOR_ACL_FILE_VIEW)) {
                return Constants.Errors.CKFINDER_CONNECTOR_ERROR_UNAUTHORIZED;
            }

            Path sourceFile = Paths.get(configuration.getTypes().get(file.getType()).getPath()
                    + file.getFolder(), file.getName());
            Path destFile = Paths.get(configuration.getTypes().get(this.type).getPath()
                    + this.currentFolder, file.getName());

            try {
                if (!Files.exists(sourceFile) || !Files.isRegularFile(sourceFile)) {
                    creator.appendErrorNodeChild(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_FILE_NOT_FOUND,
                            file.getName(), file.getFolder(), file.getType());
                    continue;
                }
                if (!this.type.equals(file.getType())) {
                    long maxSize = configuration.getTypes().get(this.type).getMaxSize();
                    if (maxSize != 0 && maxSize < Files.size(sourceFile)) {
                        creator.appendErrorNodeChild(
                                Constants.Errors.CKFINDER_CONNECTOR_ERROR_UPLOADED_TOO_BIG,
                                file.getName(), file.getFolder(), file.getType());
                        continue;
                    }
                }
                if (sourceFile.equals(destFile)) {
                    creator.appendErrorNodeChild(
                            Constants.Errors.CKFINDER_CONNECTOR_ERROR_SOURCE_AND_TARGET_PATH_EQUAL,
                            file.getName(), file.getFolder(), file.getType());
                } else if (Files.exists(destFile)) {
                    if (file.getOptions() != null
                            && file.getOptions().contains("overwrite")) {
                        if (!handleOverwrite(sourceFile, destFile)) {
                            creator.appendErrorNodeChild(
                                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
                                    file.getName(), file.getFolder(), file.getType());
                        } else {
                            this.filesCopied++;
                        }
                    } else if (file.getOptions() != null
                            && file.getOptions().contains("autorename")) {
                        if (!handleAutoRename(sourceFile, destFile)) {
                            creator.appendErrorNodeChild(
                                    Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
                                    file.getName(), file.getFolder(), file.getType());
                        } else {
                            this.filesCopied++;
                        }
                    } else {
                        creator.appendErrorNodeChild(
                                Constants.Errors.CKFINDER_CONNECTOR_ERROR_ALREADY_EXIST,
                                file.getName(), file.getFolder(), file.getType());
                    }
                } else if (FileUtils.copyFromSourceToDestFile(sourceFile, destFile,
                        false, configuration)) {
                    this.filesCopied++;
                    copyThumb(file);
                }
            } catch (SecurityException | IOException e) {
                log.error("", e);
                creator.appendErrorNodeChild(
                        Constants.Errors.CKFINDER_CONNECTOR_ERROR_ACCESS_DENIED,
                        file.getName(), file.getFolder(), file.getType());
            }
        }
        this.addCopyNode = true;
        if (creator.hasErrors()) {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_COPY_FAILED;
        } else {
            return Constants.Errors.CKFINDER_CONNECTOR_ERROR_NONE;
        }
    }

    /**
     * Handles autorename option.
     *
     * @param sourceFile source file to copy from.
     * @param destFile destination file to copy to.
     * @return true if copied correctly
     * @throws IOException when ioerror occurs
     */
    private boolean handleAutoRename(final Path sourceFile, final Path destFile)
            throws IOException {
        int counter = 1;
        Path newDestFile;
        String fileName = destFile.getFileName().toString();
        String fileNameWithoutExtension = FileUtils.getFileNameWithoutExtension(fileName, false);
        String fileExtension = FileUtils.getFileExtension(fileName, false);
        Path parent = destFile.getParent();
        while (true) {
            String newFileName = fileNameWithoutExtension
                    + "(" + counter + ")."
                    + fileExtension;
            newDestFile = parent.resolve(newFileName);
            if (!Files.exists(newDestFile)) {
                // can't be in one if=, because when error in
                // copy file occurs then it will be infinity loop
                return (FileUtils.copyFromSourceToDestFile(sourceFile,
                        newDestFile,
                        false,
                        configuration));
            } else {
                counter++;
            }
        }
    }

    /**
     * Handles overwrite option.
     *
     * @param sourceFile source file to copy from.
     * @param destFile destination file to copy to.
     * @return true if copied correctly
     * @throws IOException when ioerror occurs
     */
    private boolean handleOverwrite(final Path sourceFile, final Path destFile)
            throws IOException {
        return FileUtils.delete(destFile)
                && FileUtils.copyFromSourceToDestFile(sourceFile, destFile,
                        false, configuration);
    }

    /**
     * copy thumb file.
     *
     * @param file file to copy.
     * @throws IOException when ioerror occurs
     */
    private void copyThumb(final FilePostParam file) throws IOException {
        Path sourceThumbFile = Paths.get(configuration.getThumbsPath(),
                file.getType()
                + file.getFolder(), file.getName());
        Path destThumbFile = Paths.get(configuration.getThumbsPath(),
                this.type
                + this.currentFolder, file.getName());

        if (Files.isRegularFile(sourceThumbFile) && Files.exists(sourceThumbFile)) {
            FileUtils.copyFromSourceToDestFile(sourceThumbFile, destThumbFile,
                    false, configuration);
        }

    }

    @Override
    public void initParams(final HttpServletRequest request,
            final IConfiguration configuration,
            final Object... params) throws ConnectorException {
        super.initParams(request, configuration);
        this.files = new ArrayList<>();
        this.copiedAll = (request.getParameter("copied") != null) ? Integer.valueOf(request.getParameter("copied")) : 0;

        getFilesListFromRequest(request);

    }

    /**
     * Get list of files from request.
     *
     * @param request - request object.
     */
    private void getFilesListFromRequest(final HttpServletRequest request) {
        int i = 0;
        String paramName = "files[" + i + "][name]";
        while (request.getParameter(paramName) != null) {
            FilePostParam file = new FilePostParam();
            file.setName(getParameter(request, paramName));
            file.setFolder(getParameter(request, "files[" + i + "][folder]"));
            file.setOptions(getParameter(request, "files[" + i + "][options]"));
            file.setType(getParameter(request, "files[" + i + "][type]"));
            this.files.add(file);
            paramName = "files[" + (++i) + "][name]";
        }
    }
}
